//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.InventoryUtil;
import me.moon.util.TestUtil;
import me.moon.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class HoleFiller extends Module {
   private static HoleFiller INSTANCE = new HoleFiller();
   private final Setting<Integer> range = this.register(new Setting<>("PlaceRange", 8, 0, 10));
   private final Setting<Integer> delay = this.register(new Setting<>("Delay", 50, 0, 250));
   private final Setting<Integer> blocksPerTick = this.register(new Setting<>("BlocksPerTick", 20, 8, 30));
   private final Timer offTimer = new Timer();
   private final Timer timer = new Timer();
   private final Map<BlockPos, Integer> retries;
   private final Timer retryTimer;
   private int blocksThisTick = 0;
   private ArrayList<BlockPos> holes;

   public HoleFiller() {
      super("HoleFiller", "Fills holes around you.", Module.Category.COMBAT, true, false, true);
      this.retries = new HashMap<>();
      this.retryTimer = new Timer();
      this.holes = new ArrayList();
      this.setInstance();
   }

   public static HoleFiller getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new HoleFiller();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
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
      if (this.isOn()) {
         this.doHoleFill();
      }
   }

   @Override
   public void onDisable() {
      this.retries.clear();
   }

   private void doHoleFill() {
      if (!this.check()) {
         this.holes = new ArrayList();

         for(BlockPos pos : BlockPos.getAllInBox(
            mc.player.getPosition().add(-this.range.getValue(), -this.range.getValue(), -this.range.getValue()),
            mc.player.getPosition().add(this.range.getValue(), this.range.getValue(), this.range.getValue())
         )) {
            if (!mc.world.getBlockState(pos).getMaterial().blocksMovement()
               && !mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()) {
               boolean solidNeighbours = mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK
                     | mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN
                  && mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK
                     | mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN
                  && mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK
                     | mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN
                  && mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK
                     | mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN
                  && mc.world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR
                  && mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR
                  && mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR;
               if (solidNeighbours) {
                  this.holes.add(pos);
               }
            }
         }

         this.holes.forEach(this::placeBlock);
         this.toggle();
      }
   }

   private void placeBlock(BlockPos pos) {
      for(Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
         if (entity instanceof EntityLivingBase) {
            return;
         }
      }

      if (this.blocksThisTick < this.blocksPerTick.getValue()) {
         int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
         int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
         if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
         }

         int originalSlot = mc.player.inventory.currentItem;
         mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
         mc.playerController.updateController();
         TestUtil.placeBlock(pos);
         if (mc.player.inventory.currentItem != originalSlot) {
            mc.player.inventory.currentItem = originalSlot;
            mc.playerController.updateController();
         }

         this.timer.reset();
         ++this.blocksThisTick;
      }
   }

   private boolean check() {
      if (fullNullCheck()) {
         this.disable();
         return true;
      } else if (InventoryUtil.findHotbarBlock(BlockObsidian.class) == -1 && InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) {
         Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "Obsidian ? EnderChest ?");
         this.disable();
         return true;
      } else {
         this.blocksThisTick = 0;
         if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
         }

         return !this.timer.passedMs((long)this.delay.getValue().intValue());
      }
   }
}
