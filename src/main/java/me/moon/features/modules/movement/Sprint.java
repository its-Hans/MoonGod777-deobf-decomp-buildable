//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.movement;

import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;

public class Sprint extends Module {
   public Setting<Sprint.Mode> mode = this.register(new Setting<>("Mode", Sprint.Mode.LEGIT));
   private static Sprint INSTANCE = new Sprint();

   public Sprint() {
      super("Sprint", "Modifies sprinting", Module.Category.MOVEMENT, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Sprint getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Sprint();
      }

      return INSTANCE;
   }

   @Override
   public void onUpdate() {
      switch((Sprint.Mode)this.mode.getValue()) {
         case RAGE:
            if ((
                  mc.gameSettings.keyBindForward.isKeyDown()
                     || mc.gameSettings.keyBindBack.isKeyDown()
                     || mc.gameSettings.keyBindLeft.isKeyDown()
                     || mc.gameSettings.keyBindRight.isKeyDown()
               )
               && !mc.player.isSneaking()
               && !mc.player.collidedHorizontally
               && !((float)mc.player.getFoodStats().getFoodLevel() <= 6.0F)) {
               mc.player.setSprinting(true);
            }
            break;
         case LEGIT:
            if (mc.gameSettings.keyBindForward.isKeyDown()
               && !mc.player.isSneaking()
               && !mc.player.isHandActive()
               && !mc.player.collidedHorizontally
               && !((float)mc.player.getFoodStats().getFoodLevel() <= 6.0F)
               && mc.currentScreen == null) {
               mc.player.setSprinting(true);
            }
      }
   }

   @Override
   public void onDisable() {
      if (!nullCheck()) {
         mc.player.setSprinting(false);
      }
   }

   @Override
   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public static enum Mode {
      LEGIT,
      RAGE;
   }
}
