//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import me.moon.event.events.NoRenderEvent;
import me.moon.features.modules.misc.NoHitBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {EntityRenderer.class},
   priority = 1998
)
public class MixinEntityRenderer {
   @Redirect(
      method = {"getMouseOver"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"
)
   )
   public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
      return (List<Entity>)(!NoHitBox.getINSTANCE().isOn()
            || (!(Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) || !NoHitBox.getINSTANCE().pickaxe.getValue())
               && (
                  Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL
                     || !NoHitBox.getINSTANCE().crystal.getValue()
               )
               && (
                  Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() != Items.GOLDEN_APPLE || !NoHitBox.getINSTANCE().gapple.getValue()
               )
               && (!(Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemBlock) || !NoHitBox.getINSTANCE().block.getValue())
               && (!(Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemSword) || !NoHitBox.getINSTANCE().sword.getValue())
         ? worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate)
         : new ArrayList());
   }

   @Inject(
      method = {"hurtCameraEffect"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void hurtCameraEffect(float ticks, CallbackInfo ci) {
      NoRenderEvent event = new NoRenderEvent(1);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
      }
   }
}
