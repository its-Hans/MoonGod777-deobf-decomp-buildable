//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.moon.event.events.PacketEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.MathUtil;
import me.moon.util.Timer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blink extends Module {
   private Setting<Boolean> cPacketPlayer = this.register(new Setting<>("CPacketPlayer", true));
   private Setting<Blink.Mode> autoOff = this.register(new Setting<>("AutoOff", Blink.Mode.MANUAL));
   private Setting<Integer> timeLimit = this.register(new Setting<>("Time", 20, 1, 500, v -> this.autoOff.getValue() == Blink.Mode.TIME));
   private Setting<Integer> packetLimit = this.register(new Setting<>("Packets", 20, 1, 500, v -> this.autoOff.getValue() == Blink.Mode.PACKETS));
   private Setting<Float> distance = this.register(new Setting<>("Distance", 10.0F, 1.0F, 100.0F, v -> this.autoOff.getValue() == Blink.Mode.DISTANCE));
   private Timer timer = new Timer();
   private Queue<Packet<?>> packets = new ConcurrentLinkedQueue();
   private EntityOtherPlayerMP entity;
   private int packetsCanceled = 0;
   private BlockPos startPos = null;
   private static Blink INSTANCE = new Blink();

   public Blink() {
      super("Blink", "Fakelag.", Module.Category.PLAYER, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Blink getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Blink();
      }

      return INSTANCE;
   }

   @Override
   public void onEnable() {
      if (!fullNullCheck()) {
         this.entity = new EntityOtherPlayerMP(mc.world, mc.session.getProfile());
         this.entity.copyLocationAndAnglesFrom(mc.player);
         this.entity.rotationYaw = mc.player.rotationYaw;
         this.entity.rotationYawHead = mc.player.rotationYawHead;
         this.entity.inventory.copyInventory(mc.player.inventory);
         mc.world.addEntityToWorld(6942069, this.entity);
         this.startPos = mc.player.getPosition();
      } else {
         this.disable();
      }

      this.packetsCanceled = 0;
      this.timer.reset();
   }

   @Override
   public void onUpdate() {
      if (nullCheck()
         || this.autoOff.getValue() == Blink.Mode.TIME && this.timer.passedS((double)this.timeLimit.getValue().intValue())
         || this.autoOff.getValue() == Blink.Mode.DISTANCE
            && this.startPos != null
            && mc.player.getDistanceSq(this.startPos) >= MathUtil.square((double)this.distance.getValue().floatValue())
         || this.autoOff.getValue() == Blink.Mode.PACKETS && this.packetsCanceled >= this.packetLimit.getValue()) {
         this.disable();
      }
   }

   @Override
   public void onLogout() {
      if (this.isOn()) {
         this.disable();
      }
   }

   @SubscribeEvent
   public void onSendPacket(PacketEvent.Send event) {
      if (mc.world != null && !mc.isSingleplayer()) {
         Object packet = event.getPacket();
         if (this.cPacketPlayer.getValue() && packet instanceof CPacketPlayer) {
            event.setCanceled(true);
            this.packets.add((Packet)packet);
            ++this.packetsCanceled;
         }

         if (!this.cPacketPlayer.getValue()) {
            if (packet instanceof CPacketChatMessage
               || packet instanceof CPacketConfirmTeleport
               || packet instanceof CPacketKeepAlive
               || packet instanceof CPacketTabComplete
               || packet instanceof CPacketClientStatus) {
               return;
            }

            this.packets.add((Packet)packet);
            event.setCanceled(true);
            ++this.packetsCanceled;
         }
      }
   }

   @Override
   public void onDisable() {
      if (!fullNullCheck()) {
         mc.world.removeEntity(this.entity);

         while(!this.packets.isEmpty()) {
            mc.player.connection.sendPacket((Packet)this.packets.poll());
         }
      }

      this.startPos = null;
   }

   @Override
   public String getDisplayInfo() {
      return this.packets != null ? this.packets.size() + "" : null;
   }

   public static enum Mode {
      MANUAL,
      TIME,
      DISTANCE,
      PACKETS;
   }
}
