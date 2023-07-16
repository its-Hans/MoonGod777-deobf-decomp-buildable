//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import me.moon.event.events.ChatEvent;
import me.moon.event.events.MoveEvent;
import me.moon.event.events.PushEvent;
import me.moon.event.events.UpdateWalkingPlayerEvent;
import me.moon.features.modules.movement.Sprint;
import me.moon.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {EntityPlayerSP.class},
   priority = Integer.MAX_VALUE
)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
   public MixinEntityPlayerSP(
      Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_
   ) {
      super(p_i47378_2_, p_i47378_3_.getGameProfile());
   }

   @Inject(
      method = {"sendChatMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void sendChatMessage(String message, CallbackInfo ci) {
      ChatEvent chatEvent = new ChatEvent(message);
      MinecraftForge.EVENT_BUS.post(chatEvent);
   }

   @Inject(
      method = {"onUpdateWalkingPlayer"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void preMotion(CallbackInfo ci) {
      UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(0);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"onUpdateWalkingPlayer"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void postMotion(CallbackInfo ci) {
      UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(1);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"pushOutOfBlocks"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pushOutOfBlocksHook(double x, double y, double z, CallbackInfoReturnable<Boolean> ci) {
      PushEvent event = new PushEvent(1);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.setReturnValue(false);
      }
   }

   @Redirect(
      method = {"onLivingUpdate"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V",
   ordinal = 2
)
   )
   public void onLivingUpdate(EntityPlayerSP entityPlayerSP, boolean sprinting) {
      if (Sprint.getInstance().isOn() && Sprint.getInstance().mode.getValue() == Sprint.Mode.RAGE
         ? Util.mc.player.moveForward == 0.0F && Util.mc.player.moveStrafing == 0.0F
         : Util.mc.player.movementInput.moveStrafe == 0.0F) {
         entityPlayerSP.setSprinting(sprinting);
      } else {
         entityPlayerSP.setSprinting(true);
      }
   }

   @Inject(
      method = {"move"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void move(MoverType moverType, double n, double n2, double n3, CallbackInfo ci) {
      MoveEvent event = new MoveEvent(0, moverType, n, n2, n3);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         super.move(moverType, event.getX(), event.getY(), event.getZ());
         ci.cancel();
      }
   }
}
