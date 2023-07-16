//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import com.google.common.base.Predicate;
import java.util.List;
import me.moon.event.events.PushEvent;
import me.moon.features.modules.render.NoRender;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {World.class},
   priority = 2147483524
)
public class MixinWorld {
   @Redirect(
      method = {"getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/chunk/Chunk;getEntitiesOfTypeWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lcom/google/common/base/Predicate;)V"
)
   )
   public <T extends Entity> void getEntitiesOfTypeWithinAABBHook(
      Chunk chunk, Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter
   ) {
      try {
         chunk.getEntitiesOfTypeWithinAABB(entityClass, aabb, listToFill, filter);
      } catch (Exception var7) {
      }
   }

   @Redirect(
      method = {"handleMaterialAcceleration"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;isPushedByWater()Z"
)
   )
   public boolean isPushedbyWaterHook(Entity entity) {
      PushEvent event = new PushEvent(2, entity);
      MinecraftForge.EVENT_BUS.post(event);
      return entity.isPushedByWater() && !event.isCanceled();
   }

   @Inject(
      method = {"checkLightFor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void checkLightForHook(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
      if (NoRender.getInstance().isOn() && NoRender.getInstance().skyLightUpdate.getValue() && lightType == EnumSkyBlock.SKY) {
         ci.setReturnValue(true);
         ci.cancel();
      }
   }
}
