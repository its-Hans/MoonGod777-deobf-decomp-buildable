//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import me.moon.features.modules.render.Skeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ModelPlayer.class},
   priority = 134
)
public class MixinModelPlayer {
   @Inject(
      method = {"setRotationAngles"},
      at = {@At("RETURN")}
   )
   public void setRotationAngles(
      float limbSwing,
      float limbSwingAmount,
      float ageInTicks,
      float netHeadYaw,
      float headPitch,
      float scaleFactor,
      Entity entityIn,
      CallbackInfo callbackInfo
   ) {
      if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null && entityIn instanceof EntityPlayer) {
         Skeleton.addEntity((EntityPlayer)entityIn, (ModelPlayer)ModelPlayer.class.cast(this));
      }
   }
}
