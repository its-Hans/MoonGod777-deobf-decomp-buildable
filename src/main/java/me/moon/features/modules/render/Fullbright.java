//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.render;

import me.moon.event.events.PacketEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Fullbright extends Module {
   public Setting<Fullbright.Mode> mode = this.register(new Setting<>("Mode", Fullbright.Mode.GAMMA));
   public Setting<Boolean> effects = this.register(new Setting<>("Effects", false));
   private float previousSetting = 1.0F;

   public Fullbright() {
      super("Fullbright", "Makes your game brighter.", Module.Category.RENDER, true, false, false);
   }

   @Override
   public void onEnable() {
      this.previousSetting = mc.gameSettings.gammaSetting;
   }

   @Override
   public void onUpdate() {
      if (this.mode.getValue() == Fullbright.Mode.GAMMA) {
         mc.gameSettings.gammaSetting = 1000.0F;
      }

      if (this.mode.getValue() == Fullbright.Mode.POTION) {
         mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 5210));
      }
   }

   @Override
   public void onDisable() {
      if (this.mode.getValue() == Fullbright.Mode.POTION) {
         mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
      }

      mc.gameSettings.gammaSetting = this.previousSetting;
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof SPacketEntityEffect && this.effects.getValue()) {
         SPacketEntityEffect packet = event.getPacket();
         if (mc.player != null
            && packet.getEntityId() == mc.player.getEntityId()
            && (packet.getEffectId() == 9 || packet.getEffectId() == 15)) {
            event.setCanceled(true);
         }
      }
   }

   public static enum Mode {
      GAMMA,
      POTION;
   }
}
