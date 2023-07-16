//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.mixin.mixins;

import com.mojang.authlib.GameProfile;
import me.moon.Moon;
import me.moon.features.modules.combat.Killaura;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer extends EntityLivingBase {
   public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
      super(worldIn);
   }

   @Inject(
      method = {"getCooldownPeriod"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getCooldownPeriodHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (Killaura.getInstance().isOn() && Killaura.getInstance().tpsSync.getValue()) {
         callbackInfoReturnable.setReturnValue(
            (float)(
               1.0
                  / ((EntityPlayer)EntityPlayer.class.cast(this)).getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue()
                  * 20.0
                  * (double)Moon.serverManager.getTpsFactor()
            )
         );
      }
   }
}
