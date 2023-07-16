//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.moon.features.Feature;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionManager extends Feature {
   private final Map<EntityPlayer, PotionManager.PotionList> potions = new ConcurrentHashMap<>();

   public List<PotionEffect> getOwnPotions() {
      return this.getPlayerPotions(mc.player);
   }

   public List<PotionEffect> getPlayerPotions(EntityPlayer player) {
      PotionManager.PotionList list = this.potions.get(player);
      List<PotionEffect> potions = new ArrayList();
      if (list != null) {
         potions = list.getEffects();
      }

      return potions;
   }

   public PotionEffect[] getImportantPotions(EntityPlayer player) {
      PotionEffect[] array = new PotionEffect[3];

      for(PotionEffect effect : this.getPlayerPotions(player)) {
         Potion potion = effect.getPotion();
         String var6 = I18n.format(potion.getName(), new Object[0]).toLowerCase();
         switch(var6) {
            case "strength":
               array[0] = effect;
            case "weakness":
               array[1] = effect;
            case "speed":
               array[2] = effect;
         }
      }

      return array;
   }

   public String getPotionString(PotionEffect effect) {
      Potion potion = effect.getPotion();
      return I18n.format(potion.getName(), new Object[0])
         + " "
         + (effect.getAmplifier() + 1)
         + " "
         + ChatFormatting.WHITE
         + Potion.getPotionDurationString(effect, 1.0F);
   }

   public String getColoredPotionString(PotionEffect effect) {
      return this.getPotionString(effect);
   }

   public static class PotionList {
      private final List<PotionEffect> effects = new ArrayList();

      public void addEffect(PotionEffect effect) {
         if (effect != null) {
            this.effects.add(effect);
         }
      }

      public List<PotionEffect> getEffects() {
         return this.effects;
      }
   }
}
