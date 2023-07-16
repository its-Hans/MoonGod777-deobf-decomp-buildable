//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.movement;

import me.moon.event.events.MoveEvent;
import me.moon.event.events.PacketEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ElytraFlight extends Module {
   public Setting<Float> upSpeed = this.register(new Setting<>("UpSpeed", 2.0F, 0.0F, 3.0F));
   public Setting<Boolean> PitchSpoof = this.register(new Setting<>("PitchSpoof", true));
   public Setting<Float> oneSpeed = this.register(new Setting<>("1 Speed", 1.0F, 0.0F, 10.0F));
   public Setting<Float> twoSpeed = this.register(new Setting<>("2 Speed", 1.8000001F, 1.0F, 2.0F));

   public ElytraFlight() {
      super("ElytraFlight", "Makes Elytra Flight better.", Module.Category.MOVEMENT, true, false, false);
   }

   @SubscribeEvent
   public void onSendPacket(PacketEvent.Send event) {
      if (event.getPacket() instanceof CPacketPlayer
         && !mc.player.isElytraFlying()
         && event.getPacket() instanceof CPacketPlayer
         && mc.player.isElytraFlying()) {
         if (event.getPacket() instanceof PositionRotation) {
            PositionRotation rotation = event.getPacket();
            mc.getConnection().sendPacket(new Position(rotation.x, rotation.y, rotation.z, rotation.onGround));
            event.setCanceled(true);
         } else if (event.getPacket() instanceof Position) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      float yaw = (float)Math.toRadians((double)mc.player.rotationYaw);
      double motionA = Math.sqrt(
         mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ
      );
      if (mc.player.isElytraFlying()) {
         if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -1.0;
         } else if (!mc.player.movementInput.jump || !(motionA > 1.0)) {
            if (event.getY() != -1.01E-4) {
               event.setY(-1.01E-4);
               mc.player.motionY = -1.01E-4;
            }

            this.setMoveSpeed(event, (double)this.oneSpeed.getValue().floatValue() * 1.8);
         } else if (mc.player.motionX == 0.0 && mc.player.motionZ == 0.0) {
            mc.player.motionY = (double)this.upSpeed.getValue().floatValue();
         } else {
            double motionB = motionA * 0.008;
            mc.player.motionY += motionB * 3.2;
            mc.player.motionX -= (double)(-MathHelper.sin(yaw)) * motionB / 1.0;
            mc.player.motionZ -= (double)MathHelper.cos(yaw) * motionB / 1.0;
            mc.player.motionY *= 0.99F;
            mc.player.motionX *= 0.98F;
            mc.player.motionZ *= 0.99F;
         }
      }
   }

   private void setMoveSpeed(MoveEvent event, double speed) {
      double forward = (double)mc.player.movementInput.moveForward;
      double strafe = (double)mc.player.movementInput.moveStrafe;
      float yaw = mc.player.rotationYaw;
      if (forward == 0.0 && strafe == 0.0) {
         event.setX(0.0);
         event.setZ(0.0);
         mc.player.motionX = 0.0;
         mc.player.motionZ = 0.0;
      } else {
         if (forward != 0.0) {
            if (strafe > 0.0) {
               yaw += (float)(forward > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
               yaw += (float)(forward > 0.0 ? 45 : -45);
            }

            strafe = 0.0;
            if (forward > 0.0) {
               forward = 1.0;
            } else if (forward < 0.0) {
               forward = -1.0;
            }
         }

         double x = forward * speed * -Math.sin(Math.toRadians((double)yaw)) + strafe * speed * Math.cos(Math.toRadians((double)yaw));
         double z = forward * speed * Math.cos(Math.toRadians((double)yaw)) - strafe * speed * -Math.sin(Math.toRadians((double)yaw));
         event.setX(x);
         event.setZ(z);
         mc.player.motionX = x;
         mc.player.motionZ = z;
      }
   }

   @Override
   public void onDisable() {
      if (!fullNullCheck() && !mc.player.capabilities.isCreativeMode) {
         mc.player.capabilities.isFlying = false;
      }
   }
}
