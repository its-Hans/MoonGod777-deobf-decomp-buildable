//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.misc;

import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;
import org.apache.commons.lang3.RandomStringUtils;

public class Message extends Module {
   private final me.moon.util.Timer timer = new me.moon.util.Timer();
   private Setting<String> custom = this.register(new Setting<>("Custom", "/kit create "));
   private Setting<Integer> random = this.register(new Setting<>("Random", 1, 1, 20));
   private Setting<Integer> delay = this.register(new Setting<>("Delay", 100, 0, 10000));

   public Message() {
      super("Message", "Message", Module.Category.MISC, true, false, false);
   }

   @Override
   public void onTick() {
      if (!fullNullCheck()) {
         if (this.timer.passedMs((long)this.delay.getValue().intValue())) {
            mc.player
               .connection
               .sendPacket(new CPacketChatMessage((String)this.custom.getValue() + RandomStringUtils.randomAlphanumeric(this.random.getValue())));
            this.timer.reset();
         }
      }
   }
}
