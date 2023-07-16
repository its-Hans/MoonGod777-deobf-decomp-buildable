//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import me.moon.features.modules.misc.Interact;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {BlockLiquid.class},
   priority = 1782
)
public class MixinBlockLiquid {
   @Inject(
      method = {"canCollideCheck"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void canCollideCheck(IBlockState blockState, boolean b, CallbackInfoReturnable<Boolean> ci) {
      ci.setReturnValue(
         Interact.getInstance().isOn() && Interact.getInstance().liquid.getValue() || b && blockState.getValue(BlockLiquid.LEVEL) == 0
      );
   }
}
