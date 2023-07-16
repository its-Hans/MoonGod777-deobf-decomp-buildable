//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import me.moon.event.events.NoRenderEvent;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {LayerBipedArmor.class},
   priority = 1898
)
public class MixinLayerBipedArmor {
   @Inject(
      method = {"setModelSlotVisible"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slotIn, CallbackInfo ci) {
      NoRenderEvent event = new NoRenderEvent(0);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
         switch(slotIn) {
            case HEAD:
               model.bipedHead.showModel = false;
               model.bipedHeadwear.showModel = false;
            case CHEST:
               model.bipedBody.showModel = false;
               model.bipedRightArm.showModel = false;
               model.bipedLeftArm.showModel = false;
            case LEGS:
               model.bipedBody.showModel = false;
               model.bipedRightLeg.showModel = false;
               model.bipedLeftLeg.showModel = false;
            case FEET:
               model.bipedRightLeg.showModel = false;
               model.bipedLeftLeg.showModel = false;
         }
      }
   }
}
