//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.moon.event.events.UpdateWalkingPlayerEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.EntityUtil;
import me.moon.util.InventoryUtil;
import me.moon.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Selftrap extends Module {
   private final Setting<Integer> blocksPerTick = this.register(new Setting<>("BlocksPerTick", 8, 1, 20));
   private final Setting<Integer> delay = this.register(new Setting<>("Delay", 50, 0, 250));
   private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
   private final Setting<Integer> disableTime = this.register(new Setting<>("DisableTime", 200, 50, 1000));
   private final Setting<Boolean> disable = this.register(new Setting<>("AutoDisable", true));
   private final Setting<Boolean> packet = this.register(new Setting<>("PacketPlace", false));
   private final Timer offTimer = new Timer();
   private final Timer timer = new Timer();
   private final Map<BlockPos, Integer> retries = new HashMap<>();
   private final Timer retryTimer = new Timer();
   private int blocksThisTick = 0;
   private boolean isSneaking;
   private boolean hasOffhand = false;

   public Selftrap() {
      super("Selftrap", "Lure your enemies in!", Module.Category.COMBAT, true, false, true);
   }

   @Override
   public void onEnable() {
      if (fullNullCheck()) {
         this.disable();
      }

      this.offTimer.reset();
   }

   @Override
   public void onTick() {
      if (this.isOn() && (this.blocksPerTick.getValue() != 1 || !this.rotate.getValue())) {
         this.doHoleFill();
      }
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (this.isOn() && event.getStage() == 0 && this.blocksPerTick.getValue() == 1 && this.rotate.getValue()) {
         this.doHoleFill();
      }
   }

   @Override
   public void onDisable() {
      this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
      this.retries.clear();
      this.hasOffhand = false;
   }

   private void doHoleFill() {
      if (!this.check()) {
         for(BlockPos position : this.getPositions()) {
            int placeability = BlockUtil.isPositionPlaceable(position, false);
            if (placeability == 1 && (this.retries.get(position) == null || this.retries.get(position) < 4)) {
               this.placeBlock(position);
               this.retries.put(position, this.retries.get(position) == null ? 1 : this.retries.get(position) + 1);
            }

            if (placeability == 3) {
               this.placeBlock(position);
            }
         }
      }
   }

   private List<BlockPos> getPositions() {
      ArrayList<BlockPos> positions = new ArrayList();
      positions.add(new BlockPos(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ));
      int placeability = BlockUtil.isPositionPlaceable((BlockPos)positions.get(0), false);
      switch(placeability) {
         case 0:
            return new ArrayList();
         case 1:
            if (BlockUtil.isPositionPlaceable((BlockPos)positions.get(0), false, false) == 3) {
               return positions;
            }
         case 2:
            positions.add(new BlockPos(mc.player.posX + 1.0, mc.player.posY + 1.0, mc.player.posZ));
            positions.add(new BlockPos(mc.player.posX + 1.0, mc.player.posY + 2.0, mc.player.posZ));
         default:
            positions.sort(Comparator.comparingDouble(Vec3i::getY));
            return positions;
         case 3:
            return positions;
      }
   }

   private void placeBlock(BlockPos pos) {
      if (this.blocksThisTick < this.blocksPerTick.getValue()) {
         boolean smartRotate = this.blocksPerTick.getValue() == 1 && this.rotate.getValue();
         int originalSlot = mc.player.inventory.currentItem;
         int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
         int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
         if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
         }

         mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
         mc.playerController.updateController();
         this.isSneaking = smartRotate
            ? BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking)
            : BlockUtil.placeBlock(
               pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking
            );
         mc.player.inventory.currentItem = originalSlot;
         mc.playerController.updateController();
         this.timer.reset();
         ++this.blocksThisTick;
      }
   }

   private boolean check() {
      if (fullNullCheck()) {
         this.disable();
         return true;
      } else {
         int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
         int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
         if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
         }

         this.blocksThisTick = 0;
         this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
         if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
         }

         if (!EntityUtil.isSafe(mc.player, 0, false)) {
            this.offTimer.reset();
            return true;
         } else if (this.disable.getValue() && this.offTimer.passedMs((long)this.disableTime.getValue().intValue())) {
            this.disable();
            return true;
         } else {
            return !this.timer.passedMs((long)this.delay.getValue().intValue());
         }
      }
   }
}
