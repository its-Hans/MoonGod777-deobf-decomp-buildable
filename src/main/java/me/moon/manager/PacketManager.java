//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.manager;

import java.util.ArrayList;
import java.util.List;
import me.moon.features.Feature;
import net.minecraft.network.Packet;

public class PacketManager extends Feature {
   private final List<Packet<?>> noEventPackets = new ArrayList();

   public void sendPacketNoEvent(Packet<?> packet) {
      if (packet != null && !nullCheck()) {
         this.noEventPackets.add(packet);
         mc.player.connection.sendPacket(packet);
      }
   }

   public boolean shouldSendPacket(Packet<?> packet) {
      if (this.noEventPackets.contains(packet)) {
         this.noEventPackets.remove(packet);
         return false;
      } else {
         return true;
      }
   }
}
