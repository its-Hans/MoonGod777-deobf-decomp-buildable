//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import me.moon.features.modules.misc.ToolTips;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {GuiScreen.class},
   priority = 1998
)
public class MixinGuiScreen extends Gui {
   @Inject(
      method = {"drawWorldBackground"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void drawWorldBackground(int tint, CallbackInfo ci) {
      if (Minecraft.getMinecraft().player != null) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderToolTip"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo ci) {
      if (ToolTips.getInstance().isOn() && stack.getItem() instanceof ItemShulkerBox) {
         ToolTips.getInstance().renderShulkerToolTip(stack, x, y, null);
         ci.cancel();
      }
   }
}
