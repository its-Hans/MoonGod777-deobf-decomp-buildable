//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import me.moon.event.events.PlayerDamageBlockEvent;
import me.moon.event.events.ProcessRightClickBlockEvent;
import me.moon.features.modules.misc.PacketEat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {PlayerControllerMP.class},
   priority = Integer.MAX_VALUE
)
public abstract class MixinPlayerControllerMP {
   @Shadow
   public abstract void syncCurrentPlayItem();

   @Inject(
      method = {"onPlayerDamageBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPlayerDamageBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> ci) {
      PlayerDamageBlockEvent event = new PlayerDamageBlockEvent(0, pos, face);
      MinecraftForge.EVENT_BUS.post(event);
   }

   @Inject(
      method = {"processRightClickBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void processRightClickBlock(
      EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand, CallbackInfoReturnable<EnumActionResult> ci
   ) {
      ProcessRightClickBlockEvent event = new ProcessRightClickBlockEvent(pos, hand, Minecraft.instance.player.getHeldItem(hand));
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"onStoppedUsingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onStoppedUsingItem(EntityPlayer playerIn, CallbackInfo ci) {
      if (PacketEat.getInstance().isOn() && playerIn.getHeldItem(playerIn.getActiveHand()).getItem() instanceof ItemFood) {
         this.syncCurrentPlayItem();
         playerIn.stopActiveHand();
         ci.cancel();
      }
   }
}
