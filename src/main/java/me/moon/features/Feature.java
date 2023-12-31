//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features;

import java.util.ArrayList;
import java.util.List;
import me.moon.Moon;
import me.moon.features.gui.MoonGui;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.manager.TextManager;
import me.moon.util.Util;

public class Feature implements Util {
   public List<Setting> settings = new ArrayList<>();
   public TextManager renderer = Moon.textManager;
   private String name;

   public Feature() {
   }

   public Feature(String name) {
      this.name = name;
   }

   public static boolean nullCheck() {
      return mc.player == null;
   }

   public static boolean fullNullCheck() {
      return mc.player == null || mc.world == null;
   }

   public String getName() {
      return this.name;
   }

   public List<Setting> getSettings() {
      return this.settings;
   }

   public boolean hasSettings() {
      return !this.settings.isEmpty();
   }

   public boolean isEnabled() {
      return this instanceof Module ? ((Module)this).isOn() : false;
   }

   public boolean isDisabled() {
      return !this.isEnabled();
   }

   public Setting register(Setting setting) {
      setting.setFeature(this);
      this.settings.add(setting);
      if (this instanceof Module && mc.currentScreen instanceof MoonGui) {
         MoonGui.getInstance().updateModule((Module)this);
      }

      return setting;
   }

   public void unregister(Setting settingIn) {
      ArrayList<Setting> removeList = new ArrayList<>();

      for(Setting setting : this.settings) {
         if (setting.equals(settingIn)) {
            removeList.add(setting);
         }
      }

      if (!removeList.isEmpty()) {
         this.settings.removeAll(removeList);
      }

      if (this instanceof Module && mc.currentScreen instanceof MoonGui) {
         MoonGui.getInstance().updateModule((Module)this);
      }
   }

   public Setting getSettingByName(String name) {
      for(Setting setting : this.settings) {
         if (setting.getName().equalsIgnoreCase(name)) {
            return setting;
         }
      }

      return null;
   }

   public void reset() {
      for(Setting setting : this.settings) {
         setting.setValue(setting.getDefaultValue());
      }
   }

   public void clearSettings() {
      this.settings = new ArrayList<>();
   }
}
