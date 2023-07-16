//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import me.moon.event.events.PushEvent;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   value = {Entity.class},
   priority = 998
)
public class MixinEntity {
   @Redirect(
      method = {"applyEntityCollision"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"
)
   )
   public void addVelocityHook(Entity entity, double x, double y, double z) {
      PushEvent event = new PushEvent(0, entity, x, y, z, true);
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.isCanceled()) {
         entity.motionX += event.x;
         entity.motionY += event.y;
         entity.motionZ += event.z;
         entity.isAirBorne = event.airbone;
      }
   }
}
