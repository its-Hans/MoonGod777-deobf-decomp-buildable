//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.moon.Moon;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.EntityUtil;
import me.moon.util.InventoryUtil;
import me.moon.util.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module {
   private final Setting<Integer> blocksPerTick = this.register(new Setting<>("BlocksPerTick", 12, 1, 20));
   private final Setting<Integer> delay = this.register(new Setting<>("Delay", 0, 0, 250));
   private final Setting<Boolean> togg1e = this.register(new Setting<>("AutoToggle", false));
   private final Setting<Boolean> noGhost = this.register(new Setting<>("PacketPlace", false));
   private final Setting<Boolean> center = this.register(new Setting<>("TPCenter", false));
   private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
   private final Timer timer = new Timer();
   private final Timer retryTimer = new Timer();
   private final Set<Vec3d> extendingBlocks = new HashSet();
   private final Map<BlockPos, Integer> retries = new HashMap<>();
   private boolean isSafe;
   private BlockPos startPos;
   private boolean didPlace = false;
   private int lastHotbarSlot;
   private boolean isSneaking;
   private int placements = 0;
   private int extenders = 1;
   private int obbySlot = -1;
   private boolean offHand = false;

   public Surround() {
      super("Surround", "Surrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
   }

   @Override
   public void onEnable() {
      if (!fullNullCheck()) {
         this.lastHotbarSlot = mc.player.inventory.currentItem;
         this.startPos = EntityUtil.getRoundedBlockPos(mc.player);
         if (this.center.getValue()) {
            Moon.positionManager
               .setPositionPacket(
                  (double)this.startPos.getX() + 0.5,
                  (double)this.startPos.getY(),
                  (double)this.startPos.getZ() + 0.5,
                  true,
                  true,
                  true
               );
         }

         this.retries.clear();
         this.retryTimer.reset();
      }
   }

   @Override
   public void onTick() {
      if (!fullNullCheck()) {
         this.doFeetPlace();
      }
   }

   @Override
   public void onDisable() {
      this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
   }

   @Override
   public String getDisplayInfo() {
      return this.isSafe ? ChatFormatting.RED + "Unsafe" : ChatFormatting.GREEN + "Safe";
   }

   private void doFeetPlace() {
      if (!this.check()) {
         if (!EntityUtil.isSafe(mc.player, 0, true)) {
            this.isSafe = true;
            this.placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(mc.player, 0, true), true, false, false);
         } else {
            this.isSafe = false;
         }

         this.processExtendingBlocks();
         if (this.didPlace) {
            this.timer.reset();
         }

         if (this.togg1e.getValue()) {
            this.toggle();
         }
      }
   }

   private void processExtendingBlocks() {
      if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
         Vec3d[] array = new Vec3d[2];
         int i = 0;

         for(Iterator<Vec3d> iterator = this.extendingBlocks.iterator(); iterator.hasNext(); ++i) {
            array[i] = (Vec3d)iterator.next();
         }

         int placementsBefore = this.placements;
         if (this.areClose(array) != null) {
            this.placeBlocks(this.areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0, true), true, false, true);
         }

         if (placementsBefore < this.placements) {
            this.extendingBlocks.clear();
         }
      } else if (this.extendingBlocks.size() > 2 || this.extenders >= 1) {
         this.extendingBlocks.clear();
      }
   }

   private Vec3d areClose(Vec3d[] vec3ds) {
      int matches = 0;

      for(Vec3d vec3d : vec3ds) {
         for(Vec3d pos : EntityUtil.getUnsafeBlockArray(mc.player, 0, true)) {
            if (vec3d.equals(pos)) {
               ++matches;
            }
         }
      }

      return matches == 2 ? mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1])) : null;
   }

   private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
      boolean gotHelp = true;

      for(Vec3d vec3d : vec3ds) {
         gotHelp = true;
         BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
         switch(BlockUtil.isPositionPlaceable(position, false)) {
            case 1:
               if (this.retries.get(position) == null || this.retries.get(position) < 4) {
                  this.placeBlock(position);
                  this.retries.put(position, this.retries.get(position) == null ? 1 : this.retries.get(position) + 1);
                  this.retryTimer.reset();
               } else if (Moon.speedManager.getSpeedKpH() == 0.0 && !isExtending && this.extenders < 1) {
                  this.placeBlocks(
                     mc.player.getPositionVector().add(vec3d),
                     EntityUtil.getUnsafeBlockArrayFromVec3d(mc.player.getPositionVector().add(vec3d), 0, true),
                     hasHelpingBlocks,
                     false,
                     true
                  );
                  this.extendingBlocks.add(vec3d);
                  ++this.extenders;
               }
               break;
            case 2:
               if (!hasHelpingBlocks) {
                  break;
               }

               gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
            case 3:
               if (gotHelp) {
                  this.placeBlock(position);
               }

               if (isHelping) {
                  return true;
               }
         }
      }

      return false;
   }

   private boolean check() {
      if (fullNullCheck()) {
         if (this.isOn()) {
            this.disable();
         }

         return true;
      } else {
         int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
         int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
         if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
         }

         this.offHand = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
         this.didPlace = false;
         this.extenders = 1;
         this.placements = 0;
         this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
         int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
         if (this.isOff()) {
            return true;
         } else {
            if (this.retryTimer.passedMs(2500L)) {
               this.retries.clear();
               this.retryTimer.reset();
            }

            if (this.obbySlot == -1 && !this.offHand && echestSlot == -1) {
               Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
               this.disable();
               return true;
            } else {
               this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
               if (mc.player.inventory.currentItem != this.lastHotbarSlot
                  && mc.player.inventory.currentItem != this.obbySlot
                  && mc.player.inventory.currentItem != echestSlot) {
                  this.lastHotbarSlot = mc.player.inventory.currentItem;
               }

               if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.player))) {
                  this.disable();
                  return true;
               } else {
                  return !this.timer.passedMs((long)this.delay.getValue().intValue());
               }
            }
         }
      }
   }

   private void placeBlock(BlockPos pos) {
      if (this.placements < this.blocksPerTick.getValue()) {
         int originalSlot = mc.player.inventory.currentItem;
         int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
         int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
         if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
         }

         mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
         mc.playerController.updateController();

         for(BlockPos blockPos : new BlockPos[]{
            pos.north(), pos.south(), pos.east(), pos.west(), pos.down(), pos.up()
         }) {
            IBlockState block = mc.world.getBlockState(blockPos);
            if (block instanceof BlockAir
               || block instanceof BlockLiquid
               || block instanceof BlockTallGrass
               || block instanceof BlockFire
               || block instanceof BlockDeadBush
               || block instanceof BlockSnow) {
               this.isSneaking = BlockUtil.placeBlock(
                  pos.down(), this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.noGhost.getValue(), this.isSneaking
               );
            }
         }

         this.isSneaking = BlockUtil.placeBlock(
            pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.noGhost.getValue(), this.isSneaking
         );
         mc.player.inventory.currentItem = originalSlot;
         mc.playerController.updateController();
         this.didPlace = true;
         ++this.placements;
      }
   }
}
