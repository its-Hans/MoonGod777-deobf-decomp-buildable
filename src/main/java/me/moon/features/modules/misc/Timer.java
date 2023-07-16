package me.moon.features.modules.misc;

import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.EntityUtil;

public class Timer extends Module {
   public Setting<Float> timer = this.register(new Setting<>("Timer", 1.1F, 1.0F, 2.0F));

   public Timer() {
      super("Timer", "Timer", Module.Category.MISC, true, false, false);
   }

   @Override
   public void onTick() {
      if (!fullNullCheck()) {
         EntityUtil.setTimer(this.timer.getValue());
      }
   }

   @Override
   public void onDisable() {
      EntityUtil.resetTimer();
   }

   @Override
   public String getDisplayInfo() {
      return this.timer.getValue() + "";
   }
}
