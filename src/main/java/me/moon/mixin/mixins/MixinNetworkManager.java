package me.moon.mixin.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.moon.event.events.PacketEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {NetworkManager.class},
   priority = Integer.MAX_VALUE
)
public class MixinNetworkManager {
   @Inject(
      method = {"sendPacket(Lnet/minecraft/network/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendPacketPre(Packet<?> packet, CallbackInfo ci) {
      PacketEvent.Send event = new PacketEvent.Send(packet);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"channelRead0"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
      PacketEvent.Receive event = new PacketEvent.Receive(packet);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
      }
   }
}
