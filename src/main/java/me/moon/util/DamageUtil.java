//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class DamageUtil implements Util {
   public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
      float finalDamage = 1.0F;

      try {
         float doubleExplosionSize = 12.0F;
         double distancedSize = entity.getDistance(posX, posY, posZ) / (double)doubleExplosionSize;
         double blockDensity = (double)entity.world.getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
         double v = (1.0 - distancedSize) * blockDensity;
         float damage = (float)((int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0));
         if (entity instanceof EntityLivingBase) {
            finalDamage = getBlastReduction(
               (EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0F, false, true)
            );
         }
      } catch (NullPointerException var16) {
      }

      return finalDamage;
   }

   public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer ep = (EntityPlayer)entity;
         DamageSource ds = DamageSource.causeExplosionDamage(explosion);
         damage = CombatRules.getDamageAfterAbsorb(damage, (float)ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
         int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
         float f = MathHelper.clamp((float)k, 0.0F, 20.0F);
         damage *= 1.0F - f / 25.0F;
         if (entity.isPotionActive(Potion.getPotionById(11))) {
            damage -= damage / 4.0F;
         }

         return Math.max(damage, 0.0F);
      } else {
         return CombatRules.getDamageAfterAbsorb(
            damage, (float)entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()
         );
      }
   }

   private static float getDamageMultiplied(float damage) {
      int diff = mc.world.getDifficulty().getId();
      return damage * (diff == 0 ? 0.0F : (diff == 2 ? 1.0F : (diff == 1 ? 0.5F : 1.5F)));
   }
}
