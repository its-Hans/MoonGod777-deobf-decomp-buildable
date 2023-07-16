package me.moon.features.modules.misc;

import me.moon.features.modules.Module;

public class MultiTask extends Module {
   private static MultiTask INSTANCE = new MultiTask();

   public MultiTask() {
      super("MultiTask", "URBBRGROUG", Module.Category.MISC, true, false, false);
      this.setInstance();
   }

   public static MultiTask getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new MultiTask();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }
}
