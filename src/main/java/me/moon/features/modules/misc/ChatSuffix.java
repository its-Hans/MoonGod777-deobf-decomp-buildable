//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.misc;

import me.moon.event.events.PacketEvent;
import me.moon.features.modules.Module;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatSuffix extends Module {
   public ChatSuffix() {
      super("ChatSuffix", "ChatSuffix", Module.Category.MISC, true, false, false);
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
         CPacketChatMessage packet = event.getPacket();
         String message = packet.getMessage();
         if (message.startsWith("/")) {
            return;
         }

         message = message + "  >  Μ Ο Ο Ν";
         if (message.length() >= 256) {
            message = message.substring(0, 256);
         }

         packet.message = message;
      }
   }
}
