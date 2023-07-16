package me.moon.features.modules.misc;

import me.moon.features.modules.Module;

public class PacketEat extends Module {
   private static PacketEat INSTANCE = new PacketEat();

   public PacketEat() {
      super("PacketEat", "PacketEat", Module.Category.MISC, true, false, false);
      this.setInstance();
   }

   public static PacketEat getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new PacketEat();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }
}
