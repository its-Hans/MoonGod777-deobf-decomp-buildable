//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.moon.Moon;
import me.moon.event.events.ClientEvent;
import me.moon.features.command.Command;
import me.moon.features.gui.MoonGui;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui extends Module {
   private static ClickGui INSTANCE = new ClickGui();
   public Setting<String> prefix = this.register(new Setting<>("Prefix", "."));
   public Setting<Boolean> customFov = this.register(new Setting<>("CustomFov", false));
   public Setting<Float> fov = this.register(new Setting<>("Fov", 90.0F, -180.0F, 180.0F));
   public Setting<Integer> red = this.register(new Setting<>("Red", 255, 0, 255));
   public Setting<Integer> green = this.register(new Setting<>("Green", 255, 0, 255));
   public Setting<Integer> blue = this.register(new Setting<>("Blue", 255, 0, 255));
   public Setting<Integer> hoverAlpha = this.register(new Setting<>("Alpha", 100, 0, 255));
   public Setting<Integer> topRed = this.register(new Setting<>("SecondRed", 255, 0, 255));
   public Setting<Integer> topGreen = this.register(new Setting<>("SecondGreen", 255, 0, 255));
   public Setting<Integer> topBlue = this.register(new Setting<>("SecondBlue", 255, 0, 255));
   public Setting<Integer> alpha = this.register(new Setting<>("HoverAlpha", 50, 0, 255));
   public Setting<Boolean> rainbow = this.register(new Setting<>("Rainbow", false));
   public Setting<ClickGui.rainbowMode> rainbowModeHud = this.register(
      new Setting<>("HRainbowMode", ClickGui.rainbowMode.Static, v -> this.rainbow.getValue())
   );
   public Setting<ClickGui.rainbowModeArray> rainbowModeA = this.register(
      new Setting<>("ARainbowMode", ClickGui.rainbowModeArray.Static, v -> this.rainbow.getValue())
   );
   public Setting<Integer> rainbowHue = this.register(new Setting<>("Delay", 240, 0, 600, v -> this.rainbow.getValue()));
   public Setting<Float> rainbowBrightness = this.register(new Setting<>("Brightness ", 150.0F, 1.0F, 255.0F, v -> this.rainbow.getValue()));
   public Setting<Float> rainbowSaturation = this.register(new Setting<>("Saturation", 150.0F, 1.0F, 255.0F, v -> this.rainbow.getValue()));

   public ClickGui() {
      super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
      this.setInstance();
   }

   public static ClickGui getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ClickGui();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @Override
   public void onUpdate() {
      if (this.customFov.getValue()) {
         mc.gameSettings.setOptionFloatValue(Options.FOV, this.fov.getValue());
      }
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
         if (event.getSetting().equals(this.prefix)) {
            Moon.commandManager.setPrefix(this.prefix.getPlannedValue());
            Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + Moon.commandManager.getPrefix());
         }

         Moon.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
      }
   }

   @Override
   public void onEnable() {
      mc.displayGuiScreen(MoonGui.getClickGui());
   }

   @Override
   public void onLoad() {
      Moon.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
      Moon.commandManager.setPrefix(this.prefix.getValue());
   }

   @Override
   public void onTick() {
      if (!(mc.currentScreen instanceof MoonGui)) {
         this.disable();
      }
   }

   public static enum rainbowMode {
      Static,
      Sideway;
   }

   public static enum rainbowModeArray {
      Static,
      Up;
   }
}
