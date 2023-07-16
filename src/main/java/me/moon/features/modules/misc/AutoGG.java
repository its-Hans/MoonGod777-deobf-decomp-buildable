//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.misc;

import java.util.HashMap;
import me.moon.Moon;
import me.moon.event.events.PacketEvent;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.TextUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGG extends Module {
   private static AutoGG INSTANCE = new AutoGG();
   private Setting<String> custom = this.register(new Setting<>("Custom", "Ezzz."));
   private Setting<Boolean> totemPop$Counter = this.register(new Setting<>("TotemPop-Counter", true));
   private static final HashMap<String, Integer> totemPop = new HashMap<>();
   public String target = null;

   public AutoGG() {
      super("AutoGG", "Sends msg after you kill someone", Module.Category.MISC, true, false, false);
      this.setInstance();
   }

   public static AutoGG getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new AutoGG();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      try {
         if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35) {
               Entity entity = packet.getEntity(mc.world);
               int count = 1;
               if (totemPop.containsKey(entity.getName())) {
                  count = totemPop.get(entity.getName());
                  totemPop.put(entity.getName(), ++count);
               } else {
                  totemPop.put(entity.getName(), count);
               }

               if (this.totemPop$Counter.getValue()
                  && !entity.getName().equals(mc.player.getName())
                  && !Moon.friendManager.isFriend(entity.getName())) {
                  Command.sendMessage(
                     TextUtil.coloredString(entity.getName(), TextUtil.Color.DARK_RED)
                        + ' '
                        + TextUtil.coloredString("had popped", TextUtil.Color.RED)
                        + ' '
                        + TextUtil.coloredString(count + "", TextUtil.Color.GREEN)
                        + ' '
                        + TextUtil.coloredString("totems", TextUtil.Color.DARK_RED)
                        + ' '
                        + TextUtil.coloredString("currently.", TextUtil.Color.RED)
                  );
               }
            }
         }
      } catch (NullPointerException var5) {
      }
   }

   @Override
   public void onUpdate() {
      for(EntityPlayer player : mc.world.playerEntities) {
         if (totemPop.containsKey(player.getName()) && (player.isDead || player.getHealth() <= 0.0F)) {
            int count = totemPop.get(player.getName());
            totemPop.remove(player.getName());
            if (player.getName() == this.target) {
               mc.player
                  .connection
                  .sendPacket(new CPacketChatMessage((String)this.custom.getValue() + ' ' + this.target + " died after popping " + count + " totems."));
            }
         }
      }
   }
}
