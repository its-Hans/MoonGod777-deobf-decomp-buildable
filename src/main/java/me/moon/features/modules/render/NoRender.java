//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.render;

import me.moon.event.events.NoRenderEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender extends Module {
   private static NoRender INSTANCE = new NoRender();
   public Setting<Boolean> armor = this.register(new Setting<>("Armor", true));
   public Setting<Boolean> fire = this.register(new Setting<>("Frie", true));
   public Setting<Boolean> blind = this.register(new Setting<>("Blind", true));
   public Setting<Boolean> nausea = this.register(new Setting<>("Nausea", true));
   public Setting<Boolean> hurtCam = this.register(new Setting<>("HurtCam", true));
   public Setting<Boolean> skyLightUpdate = this.register(new Setting<>("SkyLightUpdate", true));

   public NoRender() {
      super("NoRender", "No Render", Module.Category.RENDER, true, false, false);
      this.setInstance();
   }

   public static NoRender getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new NoRender();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @Override
   public void onUpdate() {
      if (this.blind.getValue() && mc.player.isPotionActive(MobEffects.BLINDNESS)) {
         mc.player.removePotionEffect(MobEffects.BLINDNESS);
      }

      if (this.nausea.getValue() && mc.player.isPotionActive(MobEffects.NAUSEA)) {
         mc.player.removePotionEffect(MobEffects.NAUSEA);
      }
   }

   @SubscribeEvent
   public void NoRenderEventListener(NoRenderEvent event) {
      if (event.getStage() == 0 && this.armor.getValue()) {
         event.setCanceled(true);
      } else if (event.getStage() == 1 && this.hurtCam.getValue()) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void blockOverlayEventListener(RenderBlockOverlayEvent event) {
      if (this.fire.getValue() && event.getOverlayType() == OverlayType.FIRE) {
         event.setCanceled(true);
      }
   }
}
