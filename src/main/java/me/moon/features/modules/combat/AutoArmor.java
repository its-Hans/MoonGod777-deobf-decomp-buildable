//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.moon.Moon;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.InventoryUtil;
import me.moon.util.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;

public class AutoArmor extends Module {
   private final Setting<Integer> delay = this.register(new Setting<>("Delay", 50, 0, 500));
   private final Setting<Boolean> curse = this.register(new Setting<>("Vanishing", false));
   private final Setting<Boolean> mendingTakeOff = this.register(new Setting<>("AutoMend", false));
   private final Setting<Integer> closestEnemy = this.register(new Setting<>("Enemy", 8, 1, 20, v -> this.mendingTakeOff.getValue()));
   private final Setting<Integer> repair = this.register(new Setting<>("Repair%", 80, 1, 100, v -> this.mendingTakeOff.getValue()));
   private final Setting<Integer> actions = this.register(new Setting<>("Packets", 3, 1, 12));
   private final Timer timer = new Timer();
   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
   private final List<Integer> doneSlots = new ArrayList<>();
   boolean flag;

   public AutoArmor() {
      super("AutoArmor", "Puts Armor on for you.", Module.Category.COMBAT, true, false, false);
   }

   @Override
   public void onLogin() {
      this.timer.reset();
   }

   @Override
   public void onDisable() {
      this.taskList.clear();
      this.doneSlots.clear();
      this.flag = false;
   }

   @Override
   public void onLogout() {
      this.taskList.clear();
      this.doneSlots.clear();
   }

   @Override
   public void onTick() {
      if (!fullNullCheck() && (!(mc.currentScreen instanceof GuiContainer) || mc.currentScreen instanceof GuiInventory)) {
         if (this.taskList.isEmpty()) {
            if (this.mendingTakeOff.getValue()
               && InventoryUtil.holdingItem(ItemExpBottle.class)
               && mc.gameSettings.keyBindUseItem.isKeyDown()
               && mc.world
                  .playerEntities
                  .stream()
                  .noneMatch(
                     e -> e != mc.player
                           && !Moon.friendManager.isFriend(e.getName())
                           && mc.player.getDistance(e) <= (float)this.closestEnemy.getValue().intValue()
                  )
               && !this.flag) {
               int takeOff = 0;

               for(Entry<Integer, ItemStack> armorSlot : this.getArmor().entrySet()) {
                  ItemStack stack = (ItemStack)armorSlot.getValue();
                  float percent = (float)this.repair.getValue().intValue() / 100.0F;
                  int dam = Math.round((float)stack.getMaxDamage() * percent);
                  if (dam < stack.getMaxDamage() - stack.getItemDamage()) {
                     ++takeOff;
                  }
               }

               if (takeOff == 4) {
                  this.flag = true;
               }

               if (!this.flag) {
                  ItemStack itemStack1 = mc.player.inventoryContainer.getSlot(5).getStack();
                  if (!itemStack1.isEmpty) {
                     float percent = (float)this.repair.getValue().intValue() / 100.0F;
                     int dam2 = Math.round((float)itemStack1.getMaxDamage() * percent);
                     if (dam2 < itemStack1.getMaxDamage() - itemStack1.getItemDamage()) {
                        this.takeOffSlot(5);
                     }
                  }

                  ItemStack itemStack2 = mc.player.inventoryContainer.getSlot(6).getStack();
                  if (!itemStack2.isEmpty) {
                     float percent = (float)this.repair.getValue().intValue() / 100.0F;
                     int dam3 = Math.round((float)itemStack2.getMaxDamage() * percent);
                     if (dam3 < itemStack2.getMaxDamage() - itemStack2.getItemDamage()) {
                        this.takeOffSlot(6);
                     }
                  }

                  ItemStack itemStack3 = mc.player.inventoryContainer.getSlot(7).getStack();
                  if (!itemStack3.isEmpty) {
                     float percent = (float)this.repair.getValue().intValue() / 100.0F;
                     int dam = Math.round((float)itemStack3.getMaxDamage() * percent);
                     if (dam < itemStack3.getMaxDamage() - itemStack3.getItemDamage()) {
                        this.takeOffSlot(7);
                     }
                  }

                  ItemStack itemStack4 = mc.player.inventoryContainer.getSlot(8).getStack();
                  if (!itemStack4.isEmpty) {
                     float percent = (float)this.repair.getValue().intValue() / 100.0F;
                     int dam4 = Math.round((float)itemStack4.getMaxDamage() * percent);
                     if (dam4 < itemStack4.getMaxDamage() - itemStack4.getItemDamage()) {
                        this.takeOffSlot(8);
                     }
                  }
               }

               return;
            }

            this.flag = false;
            ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
            int slot4;
            if (helm.getItem() == Items.AIR
               && (slot4 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, this.curse.getValue(), true)) != -1) {
               this.getSlotOn(5, slot4);
            }

            int slot3;
            if (mc.player.inventoryContainer.getSlot(6).getStack().getItem() == Items.AIR
               && (slot3 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, this.curse.getValue(), true)) != -1) {
               this.getSlotOn(6, slot3);
            }

            int slot2;
            if (mc.player.inventoryContainer.getSlot(7).getStack().getItem() == Items.AIR
               && (slot2 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, this.curse.getValue(), true)) != -1) {
               this.getSlotOn(7, slot2);
            }

            int slot;
            if (mc.player.inventoryContainer.getSlot(8).getStack().getItem() == Items.AIR
               && (slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, this.curse.getValue(), true)) != -1) {
               this.getSlotOn(8, slot);
            }
         }

         if (this.timer.passedMs((long)((int)((float)this.delay.getValue().intValue() * Moon.serverManager.getTpsFactor())))) {
            if (!this.taskList.isEmpty()) {
               for(int i = 0; i < this.actions.getValue(); ++i) {
                  InventoryUtil.Task task = this.taskList.poll();
                  if (task != null) {
                     task.run();
                  }
               }
            }

            this.timer.reset();
         }
      }
   }

   private void takeOffSlot(int slot) {
      if (this.taskList.isEmpty()) {
         int target = -1;

         for(int i : InventoryUtil.findEmptySlots(true)) {
            if (!this.doneSlots.contains(target)) {
               target = i;
               this.doneSlots.add(i);
            }
         }

         if (target != -1) {
            this.taskList.add(new InventoryUtil.Task(slot));
            this.taskList.add(new InventoryUtil.Task(target));
            this.taskList.add(new InventoryUtil.Task());
         }
      }
   }

   private void getSlotOn(int slot, int target) {
      if (this.taskList.isEmpty()) {
         this.doneSlots.remove(Integer.valueOf(target));
         this.taskList.add(new InventoryUtil.Task(target));
         this.taskList.add(new InventoryUtil.Task(slot));
         this.taskList.add(new InventoryUtil.Task());
      }
   }

   private Map<Integer, ItemStack> getArmor() {
      return this.getInventorySlots(5, 8);
   }

   private Map<Integer, ItemStack> getInventorySlots(int current, int last) {
      HashMap<Integer, ItemStack> fullInventorySlots;
      for(fullInventorySlots = new HashMap<>(); current <= last; ++current) {
         fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
      }

      return fullInventorySlots;
   }
}
