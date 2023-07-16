//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import me.moon.features.modules.misc.MultiTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   value = {Minecraft.class},
   priority = 234
)
public abstract class MixinMinecraft {
   @Redirect(
      method = {"sendClickBlockToController"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"
)
   )
   private boolean isHandActiveWrapper(EntityPlayerSP playerSP) {
      return !MultiTask.getInstance().isOn() && playerSP.isHandActive();
   }

   @Redirect(
      method = {"rightClickMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z",
   ordinal = 0
)
   )
   private boolean isHittingBlockHook(PlayerControllerMP playerControllerMP) {
      return !MultiTask.getInstance().isOn() && playerControllerMP.getIsHittingBlock();
   }
}
