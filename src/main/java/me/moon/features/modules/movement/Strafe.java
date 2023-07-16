//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.movement;

import java.util.Objects;
import me.moon.event.events.MoveEvent;
import me.moon.event.events.UpdateWalkingPlayerEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Strafe extends Module {
   public Setting<Strafe.Mode> mode = this.register(new Setting<>("Mode", Strafe.Mode.NORMAL));
   private static Strafe INSTANCE = new Strafe();
   private double lastDist;
   private double moveSpeed;
   int stage;

   public Strafe() {
      super("Strafe", "Modifies sprinting", Module.Category.MOVEMENT, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Strafe getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Strafe();
      }

      return INSTANCE;
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
      if (event.getStage() != 1 || !fullNullCheck()) {
         this.lastDist = Math.sqrt(
            (mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX)
               + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ)
         );
      }
   }

   @SubscribeEvent
   public void onStrafe(MoveEvent event) {
      if (!fullNullCheck()) {
         if (!mc.player.isInWater() && !mc.player.isInLava()) {
            if (mc.player.onGround) {
               this.stage = 2;
            }

            switch(this.stage) {
               case 0:
                  ++this.stage;
                  this.lastDist = 0.0;
                  break;
               case 1:
               default:
                  if ((
                        mc.world
                                 .getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0))
                                 .size()
                              > 0
                           || mc.player.collidedVertically
                     )
                     && this.stage > 0) {
                     this.stage = mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F ? 0 : 1;
                  }

                  this.moveSpeed = this.lastDist - this.lastDist / (this.mode.getValue() == Strafe.Mode.NORMAL ? 730.0 : 159.0);
                  break;
               case 2:
                  double motionY = 0.40123128;
                  if (mc.player.onGround && mc.gameSettings.keyBindJump.isKeyDown()) {
                     if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                        motionY += (double)((float)(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                     }

                     event.setY(mc.player.motionY = motionY);
                     this.moveSpeed *= this.mode.getValue() == Strafe.Mode.NORMAL ? 1.67 : 2.149;
                  }
                  break;
               case 3:
                  this.moveSpeed = this.lastDist - (this.mode.getValue() == Strafe.Mode.NORMAL ? 0.6896 : 0.795) * (this.lastDist - this.getBaseMoveSpeed());
            }

            if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.player.onGround) {
               this.moveSpeed = this.getBaseMoveSpeed();
            } else {
               this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
            }

            double n = (double)mc.player.movementInput.moveForward;
            double n2 = (double)mc.player.movementInput.moveStrafe;
            double n3 = (double)mc.player.rotationYaw;
            if (n == 0.0 && n2 == 0.0) {
               event.setX(0.0);
               event.setZ(0.0);
            } else if (n != 0.0 && n2 != 0.0) {
               n *= Math.sin(Math.PI / 4);
               n2 *= Math.cos(Math.PI / 4);
            }

            double n4 = this.mode.getValue() == Strafe.Mode.NORMAL ? 0.993 : 0.99;
            event.setX((n * this.moveSpeed * -Math.sin(Math.toRadians(n3)) + n2 * this.moveSpeed * Math.cos(Math.toRadians(n3))) * n4);
            event.setZ((n * this.moveSpeed * Math.cos(Math.toRadians(n3)) - n2 * this.moveSpeed * -Math.sin(Math.toRadians(n3))) * n4);
            ++this.stage;
            event.setCanceled(true);
         }
      }
   }

   public double getBaseMoveSpeed() {
      double n = 0.2873;
      if (mc.player.isPotionActive(MobEffects.SPEED)) {
         n *= 1.0 + 0.2 * (double)(((PotionEffect)Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier() + 1);
      }

      return n;
   }

   @Override
   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public static enum Mode {
      NORMAL,
      Strict;
   }
}
