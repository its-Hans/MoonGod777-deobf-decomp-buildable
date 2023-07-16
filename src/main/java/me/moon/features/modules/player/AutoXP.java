//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.player;

import java.util.List;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.InventoryUtil;
import me.moon.util.Timer;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumHand;

public class AutoXP extends Module {
   char toMend = 0;
   private final Timer timer = new Timer();
   private Setting<Integer> delay = this.register(new Setting<>("XP Delay", 4, 1, 4));
   private final Setting<Integer> minDamage = this.register(new Setting<>("Min Damage", 50, 0, 100));
   private final Setting<Integer> maxHeal = this.register(new Setting<>("Repair To", 90, 0, 100));
   private Setting<Boolean> sneakOnly = this.register(new Setting<>("Sneak Only", true));
   private Setting<Boolean> predict = this.register(new Setting<>("Predict", true));

   public AutoXP() {
      super("AutoXP", "AutoXP", Module.Category.PLAYER, true, false, false);
   }

   @Override
   public void onUpdate() {
      if (!fullNullCheck()) {
         int sumOfDamage = 0;
         List<ItemStack> armour = mc.player.inventory.armorInventory;

         for(int i = 0; i < armour.size(); ++i) {
            ItemStack itemStack = (ItemStack)armour.get(i);
            if (!itemStack.isEmpty) {
               float damageOnArmor = (float)(itemStack.getMaxDamage() - itemStack.getItemDamage());
               float damagePercent = 100.0F - 100.0F * (1.0F - damageOnArmor / (float)itemStack.getMaxDamage());
               if (damagePercent <= (float)this.maxHeal.getValue().intValue()) {
                  if (damagePercent <= (float)this.minDamage.getValue().intValue()) {
                     this.toMend = (char)(this.toMend | 1 << i);
                  }

                  if (this.predict.getValue()) {
                     sumOfDamage = (int)(
                        (float)sumOfDamage
                           + (
                              (float)(itemStack.getMaxDamage() * this.maxHeal.getValue()) / 100.0F
                                 - (float)(itemStack.getMaxDamage() - itemStack.getItemDamage())
                           )
                     );
                  }
               } else {
                  this.toMend = (char)(this.toMend & ~(1 << i));
               }
            }
         }

         if (this.toMend > 0 && this.timer.passedMs((long)(this.delay.getValue() * 45))) {
            this.timer.reset();
            if (this.predict.getValue()) {
               int totalXp = mc.world
                  .loadedEntityList
                  .stream()
                  .filter(entity -> entity instanceof EntityXPOrb)
                  .filter(entity -> entity.getDistanceSq(mc.player) <= 1.0)
                  .mapToInt(entity -> ((EntityXPOrb)entity).xpValue)
                  .sum();
               if (totalXp * 2 < sumOfDamage) {
                  this.mendArmor();
               }
            } else {
               this.mendArmor();
            }
         }
      }
   }

   private void mendArmor() {
      if (!this.sneakOnly.getValue() || mc.player.isSneaking()) {
         int a = InventoryUtil.getItemHotbar(Items.EXPERIENCE_BOTTLE);
         int b = mc.player.inventory.currentItem;
         if (a != -1) {
            mc.player.connection.sendPacket(new Rotation(mc.player.rotationYaw, 90.0F, true));
            mc.player.inventory.currentItem = a;
            mc.playerController.updateController();
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.player.inventory.currentItem = b;
            mc.playerController.updateController();
         }
      }
   }
}
