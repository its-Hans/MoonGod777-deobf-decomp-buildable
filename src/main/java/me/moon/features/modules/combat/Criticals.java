//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import java.util.Objects;
import me.moon.event.events.PacketEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.EntityUtil;
import me.moon.util.Timer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {
   private final Setting<Integer> packets = this.register(new Setting<>("Packets", 2, 1, 5));
   private Setting<Integer> desyncDelay = this.register(new Setting<>("DesyncDelay", 10, 0, 500));
   private Setting<Boolean> onlySharp = this.register(new Setting<>("SwordOnly", true));
   private final Timer timer = new Timer();
   private final boolean resetTimer = false;

   public Criticals() {
      super("Criticals", "Scores criticals for you", Module.Category.COMBAT, true, false, false);
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      CPacketUseEntity packet;
      if (event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == Action.ATTACK) {
         if (fullNullCheck()) {
            return;
         }

         if (this.onlySharp.getValue() && !EntityUtil.holdingWeapon(mc.player)) {
            return;
         }

         this.getClass();
         if (!this.timer.passedMs((long)this.desyncDelay.getValue().intValue())) {
            return;
         }

         if (mc.player.onGround
            && !mc.gameSettings.keyBindJump.isKeyDown()
            && packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase
            && !mc.player.isInWater()
            && !mc.player.isInLava()) {
            switch(this.packets.getValue()) {
               case 1:
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY + 0.1F, mc.player.posZ, false));
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  break;
               case 2:
                  mc.player
                     .connection
                     .sendPacket(
                        new Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false)
                     );
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  mc.player
                     .connection
                     .sendPacket(
                        new Position(mc.player.posX, mc.player.posY + 1.1E-5, mc.player.posZ, false)
                     );
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  break;
               case 3:
                  mc.player
                     .connection
                     .sendPacket(
                        new Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false)
                     );
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  mc.player
                     .connection
                     .sendPacket(
                        new Position(mc.player.posX, mc.player.posY + 0.0125, mc.player.posZ, false)
                     );
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  break;
               case 4:
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY + 0.05, mc.player.posZ, false));
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY + 0.03, mc.player.posZ, false));
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  break;
               case 5:
                  mc.player
                     .connection
                     .sendPacket(
                        new Position(mc.player.posX, mc.player.posY + 0.1625, mc.player.posZ, false)
                     );
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  mc.player
                     .connection
                     .sendPacket(
                        new Position(mc.player.posX, mc.player.posY + 4.0E-6, mc.player.posZ, false)
                     );
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  mc.player
                     .connection
                     .sendPacket(
                        new Position(mc.player.posX, mc.player.posY + 1.0E-6, mc.player.posZ, false)
                     );
                  mc.player
                     .connection
                     .sendPacket(new Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                  mc.player.connection.sendPacket(new CPacketPlayer());
                  mc.player.onCriticalHit(Objects.requireNonNull(packet.getEntityFromWorld(mc.world)));
            }

            this.timer.reset();
         }
      }
   }

   @Override
   public String getDisplayInfo() {
      return "Packet";
   }
}
