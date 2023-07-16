//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.misc;

import me.moon.event.events.PacketEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Interact extends Module {
   private static Interact INSTANCE = new Interact();
   public Setting<Boolean> buildHeight = this.register(new Setting<>("BuildHeight", Boolean.FALSE));
   public Setting<Boolean> liquid = this.register(new Setting<>("Liquid", Boolean.FALSE));

   public Interact() {
      super("Interact", "ForceInteract", Module.Category.MISC, true, false, false);
      this.setInstance();
   }

   public static Interact getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Interact();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (this.buildHeight.getValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
         CPacketPlayerTryUseItemOnBlock packet = event.getPacket();
         if (packet.getPos().getY() >= 255 && packet.getDirection() == EnumFacing.UP) {
            packet.placedBlockDirection = EnumFacing.DOWN;
         }
      }
   }
}
