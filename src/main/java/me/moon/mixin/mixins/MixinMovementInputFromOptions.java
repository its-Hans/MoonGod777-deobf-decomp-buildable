//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import me.moon.features.modules.movement.PlayerTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   value = {MovementInputFromOptions.class},
   priority = 11010
)
public abstract class MixinMovementInputFromOptions extends MovementInput {
   @Redirect(
      method = {"updatePlayerMoveState"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"
)
   )
   public boolean isKeyPressed(KeyBinding keyBinding) {
      int keyCode = keyBinding.getKeyCode();
      return keyCode > 0
            && keyCode < 256
            && PlayerTweaks.getInstance().isOn()
            && PlayerTweaks.getInstance().guiMove.getValue()
            && Minecraft.getMinecraft().currentScreen != null
            && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)
         ? Keyboard.isKeyDown(keyCode)
         : keyBinding.isKeyDown();
   }
}
