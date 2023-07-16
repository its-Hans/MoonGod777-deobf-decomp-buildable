//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.movement;

import me.moon.event.events.PacketEvent;
import me.moon.event.events.PushEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerTweaks extends Module {
   public Setting<Boolean> noSlow = this.register(new Setting<>("No Slow", true));
   public Setting<Boolean> antiKnockBack = this.register(new Setting<>("Velocity", true));
   public Setting<Boolean> noEntityPush = this.register(new Setting<>("No PlayerPush", true));
   public Setting<Boolean> noBlockPush = this.register(new Setting<>("No BlockPush", true));
   public Setting<Boolean> noWaterPush = this.register(new Setting<>("No LiquidPush", true));
   public Setting<Boolean> guiMove = this.register(new Setting<>("Gui Move", true));
   public static PlayerTweaks INSTANCE = new PlayerTweaks();

   public PlayerTweaks() {
      super("PlayerTweaks", "XD", Module.Category.MOVEMENT, true, false, false);
      this.setInstance();
   }

   public static PlayerTweaks getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new PlayerTweaks();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @SubscribeEvent
   public void Slow(InputUpdateEvent event) {
      if (this.noSlow.getValue() && mc.player.isHandActive() && !mc.player.isRiding()) {
         MovementInput var10000 = event.getMovementInput();
         var10000.moveStrafe *= 5.0F;
         var10000 = event.getMovementInput();
         var10000.moveForward *= 5.0F;
      }
   }

   @SubscribeEvent
   public void onPacketReceived(PacketEvent.Receive event) {
      if (!fullNullCheck()) {
         if (this.antiKnockBack.getValue()) {
            if (event.getPacket() instanceof SPacketEntityVelocity
               && ((SPacketEntityVelocity)event.getPacket()).getEntityID() == mc.player.getEntityId()) {
               event.setCanceled(true);
            }

            if (event.getPacket() instanceof SPacketExplosion) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public void onPush(PushEvent event) {
      if (!fullNullCheck()) {
         if (event.getStage() == 0 && this.noEntityPush.getValue() && event.entity.equals(mc.player)) {
            event.x = -event.x * 0.0;
            event.y = -event.y * 0.0;
            event.z = -event.z * 0.0;
         } else if (event.getStage() == 1 && this.noBlockPush.getValue()) {
            event.setCanceled(true);
         } else if (event.getStage() == 2 && this.noWaterPush.getValue() && mc.player != null && mc.player.equals(event.entity)) {
            event.setCanceled(true);
         }
      }
   }
}
