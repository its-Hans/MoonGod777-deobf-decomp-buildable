//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.moon.event.events.PacketEvent;
import me.moon.event.events.ProcessRightClickBlockEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.DamageUtil;
import me.moon.util.EntityUtil;
import me.moon.util.InventoryUtil;
import me.moon.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class Offhand extends Module {
   private static Offhand instance;
   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
   private final Timer timer = new Timer();
   private final Timer secondTimer = new Timer();
   public Setting<Boolean> crystal = this.register(new Setting<>("Crystal", true));
   public Setting<Float> crystalHealth = this.register(new Setting<>("CrystalHP", 13.0F, 0.1F, 36.0F));
   public Setting<Float> crystalHoleHealth = this.register(new Setting<>("CrystalHoleHP", 3.5F, 0.1F, 36.0F));
   public Setting<Boolean> swordGapple = this.register(new Setting<>("SwordGapple", true));
   public Setting<Boolean> crystalCheck = this.register(new Setting<>("CrystalCheck", true));
   public Setting<Boolean> armorCheck = this.register(new Setting<>("ArmorCheck", true));
   public Setting<Integer> actions = this.register(new Setting<>("Packets", 4, 1, 4));
   public Offhand.Mode2 currentMode = Offhand.Mode2.TOTEMS;
   public int totems = 0;
   public int crystals = 0;
   public int gapples = 0;
   public int lastTotemSlot = -1;
   public int lastGappleSlot = -1;
   public int lastCrystalSlot = -1;
   public int lastObbySlot = -1;
   public int lastWebSlot = -1;
   public boolean holdingCrystal = false;
   public boolean holdingTotem = false;
   public boolean holdingGapple = false;
   public boolean didSwitchThisTick = false;
   private boolean second = false;
   private boolean switchedForHealthReason = false;

   public Offhand() {
      super("Offhand", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
      instance = this;
   }

   public static Offhand getInstance() {
      if (instance == null) {
         instance = new Offhand();
      }

      return instance;
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
      if (event.hand == EnumHand.MAIN_HAND
         && event.stack.getItem() == Items.END_CRYSTAL
         && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE
         && mc.objectMouseOver != null
         && event.pos == mc.objectMouseOver.getBlockPos()) {
         event.setCanceled(true);
         mc.player.setActiveHand(EnumHand.OFF_HAND);
         mc.playerController.processRightClick(mc.player, mc.world, EnumHand.OFF_HAND);
      }
   }

   @Override
   public void onUpdate() {
      if (this.timer.passedMs(50L)) {
         if (mc.player != null
            && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE
            && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL
            && Mouse.isButtonDown(1)) {
            mc.player.setActiveHand(EnumHand.OFF_HAND);
            mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
         }
      } else if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE
         && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
         mc.gameSettings.keyBindUseItem.pressed = false;
      }

      if (!nullCheck()) {
         this.doOffhand();
         if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
         }
      }
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck()
         && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE
         && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL
         && mc.gameSettings.keyBindUseItem.isKeyDown()) {
         if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet2 = event.getPacket();
            if (packet2.getHand() == EnumHand.MAIN_HAND) {
               if (this.timer.passedMs(50L)) {
                  mc.player.setActiveHand(EnumHand.OFF_HAND);
                  mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
               }

               event.setCanceled(true);
            }
         } else if (event.getPacket() instanceof CPacketPlayerTryUseItem
            && ((CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.OFF_HAND
            && !this.timer.passedMs(50L)) {
            event.setCanceled(true);
         }
      }
   }

   @Override
   public String getDisplayInfo() {
      if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
         return "Crystals";
      } else if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
         return "Totems";
      } else {
         return mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE ? "Gapples" : null;
      }
   }

   public void doOffhand() {
      this.didSwitchThisTick = false;
      this.holdingCrystal = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
      this.holdingTotem = mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING;
      this.holdingGapple = mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE;
      this.totems = mc.player
         .inventory
         .mainInventory
         .stream()
         .filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING)
         .mapToInt(ItemStack::getCount)
         .sum();
      if (this.holdingTotem) {
         this.totems += mc.player
            .inventory
            .offHandInventory
            .stream()
            .filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING)
            .mapToInt(ItemStack::getCount)
            .sum();
      }

      this.crystals = mc.player
         .inventory
         .mainInventory
         .stream()
         .filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL)
         .mapToInt(ItemStack::getCount)
         .sum();
      if (this.holdingCrystal) {
         this.crystals += mc.player
            .inventory
            .offHandInventory
            .stream()
            .filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL)
            .mapToInt(ItemStack::getCount)
            .sum();
      }

      this.gapples = mc.player
         .inventory
         .mainInventory
         .stream()
         .filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE)
         .mapToInt(ItemStack::getCount)
         .sum();
      if (this.holdingGapple) {
         this.gapples += mc.player
            .inventory
            .offHandInventory
            .stream()
            .filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE)
            .mapToInt(ItemStack::getCount)
            .sum();
      }

      this.doSwitch();
   }

   public void doSwitch() {
      this.currentMode = Offhand.Mode2.TOTEMS;
      if (this.swordGapple.getValue()
         && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword
         && mc.gameSettings.keyBindUseItem.isKeyDown()) {
         this.currentMode = Offhand.Mode2.GAPPLES;
      } else if (this.currentMode != Offhand.Mode2.CRYSTALS
         && this.crystal.getValue()
         && (
            EntityUtil.isSafe(mc.player, 0, false)
                  && mc.player.getHealth() + mc.player.getAbsorptionAmount() > this.crystalHoleHealth.getValue()
               || mc.player.getHealth() + mc.player.getAbsorptionAmount() > this.crystalHealth.getValue()
         )) {
         this.currentMode = Offhand.Mode2.CRYSTALS;
      }

      if (this.currentMode == Offhand.Mode2.CRYSTALS && this.crystals == 0) {
         this.setMode(Offhand.Mode2.TOTEMS);
      }

      if (this.currentMode == Offhand.Mode2.CRYSTALS
         && (
            !EntityUtil.isSafe(mc.player, 0, false)
                  && mc.player.getHealth() + mc.player.getAbsorptionAmount() <= this.crystalHealth.getValue()
               || mc.player.getHealth() + mc.player.getAbsorptionAmount() <= this.crystalHoleHealth.getValue()
         )) {
         if (this.currentMode == Offhand.Mode2.CRYSTALS) {
            this.switchedForHealthReason = true;
         }

         this.setMode(Offhand.Mode2.TOTEMS);
      }

      if (this.crystalCheck.getValue()) {
         for(Entity crystal : mc.world.loadedEntityList) {
            if (crystal != null
               && crystal instanceof EntityEnderCrystal
               && !crystal.isDead
               && !(Math.sqrt(mc.player.getDistanceSq(crystal.posX, crystal.posY, crystal.posZ)) > 12.0)
               && !mc.player.isDead
               && !(mc.player.getHealth() + mc.player.getAbsorptionAmount() <= 0.0F)) {
               double selfDamage = (double)DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, mc.player);
               if (selfDamage + 2.0 >= (double)(mc.player.getHealth() + mc.player.getAbsorptionAmount())
                  || selfDamage - 0.5 > (double)this.crystalHealth.getValue().floatValue()) {
                  this.setMode(Offhand.Mode2.TOTEMS);
               }
            }
         }
      }

      if (this.switchedForHealthReason
         && (
            EntityUtil.isSafe(mc.player, 0, false)
                  && mc.player.getHealth() + mc.player.getAbsorptionAmount() > this.crystalHoleHealth.getValue()
               || mc.player.getHealth() + mc.player.getAbsorptionAmount() > this.crystalHealth.getValue()
         )) {
         this.setMode(Offhand.Mode2.CRYSTALS);
         this.switchedForHealthReason = false;
      }

      if (this.currentMode == Offhand.Mode2.CRYSTALS
         && this.armorCheck.getValue()
         && (
            mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR
               || mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR
               || mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR
               || mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR
         )) {
         this.setMode(Offhand.Mode2.TOTEMS);
      }

      if (!(mc.currentScreen instanceof GuiContainer) || mc.currentScreen instanceof GuiInventory) {
         Item currentOffhandItem = mc.player.getHeldItemOffhand().getItem();
         switch(this.currentMode) {
            case TOTEMS:
               if (this.totems > 0 && !this.holdingTotem) {
                  this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                  int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                  this.putItemInOffhand(this.lastTotemSlot, lastSlot);
               }
               break;
            case GAPPLES:
               if (this.gapples > 0 && !this.holdingGapple) {
                  this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                  int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                  this.putItemInOffhand(this.lastGappleSlot, lastSlot);
               }
               break;
            default:
               if (this.crystals > 0 && !this.holdingCrystal) {
                  this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                  int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                  this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
               }
         }

         for(int i = 0; i < this.actions.getValue(); ++i) {
            InventoryUtil.Task task = this.taskList.poll();
            if (task != null) {
               task.run();
               if (task.isSwitching()) {
                  this.didSwitchThisTick = true;
               }
            }
         }
      }
   }

   private int getLastSlot(Item item, int slotIn) {
      if (item == Items.END_CRYSTAL) {
         return this.lastCrystalSlot;
      } else if (item == Items.GOLDEN_APPLE) {
         return this.lastGappleSlot;
      } else if (item == Items.TOTEM_OF_UNDYING) {
         return this.lastTotemSlot;
      } else if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
         return this.lastObbySlot;
      } else if (InventoryUtil.isBlock(item, BlockWeb.class)) {
         return this.lastWebSlot;
      } else {
         return item == Items.AIR ? -1 : slotIn;
      }
   }

   private void putItemInOffhand(int slotIn, int slotOut) {
      if (slotIn != -1 && this.taskList.isEmpty()) {
         this.taskList.add(new InventoryUtil.Task(slotIn));
         this.taskList.add(new InventoryUtil.Task(45));
         this.taskList.add(new InventoryUtil.Task(slotOut));
         this.taskList.add(new InventoryUtil.Task());
      }
   }

   public void setMode(Offhand.Mode2 mode) {
      this.currentMode = this.currentMode == mode ? Offhand.Mode2.TOTEMS : mode;
   }

   public static enum Mode2 {
      TOTEMS,
      GAPPLES,
      CRYSTALS;
   }
}
