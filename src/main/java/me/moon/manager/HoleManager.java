//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.moon.features.Feature;
import me.moon.util.BlockUtil;
import me.moon.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleManager extends Feature {
   private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
   private final List<BlockPos> midSafety = new ArrayList();
   private List<BlockPos> holes = new ArrayList();

   public void update() {
      if (!fullNullCheck()) {
         this.holes = this.calcHoles();
      }
   }

   public List<BlockPos> getHoles() {
      return this.holes;
   }

   public List<BlockPos> getMidSafety() {
      return this.midSafety;
   }

   public List<BlockPos> getSortedHoles() {
      this.holes.sort(Comparator.comparingDouble(hole -> mc.player.getDistanceSq(hole)));
      return this.getHoles();
   }

   public List<BlockPos> calcHoles() {
      ArrayList<BlockPos> safeSpots = new ArrayList();
      this.midSafety.clear();

      for(BlockPos pos : BlockUtil.getSphere(EntityUtil.getPlayerPos(mc.player), 6.0F, 6, false, true, 0)) {
         if (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)
            && mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)
            && mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
            boolean isSafe = true;
            boolean midSafe = true;

            for(BlockPos offset : surroundOffset) {
               Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
               if (BlockUtil.isBlockUnSolid(block)) {
                  midSafe = false;
               }

               if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                  isSafe = false;
               }
            }

            if (isSafe) {
               safeSpots.add(pos);
            }

            if (midSafe) {
               this.midSafety.add(pos);
            }
         }
      }

      return safeSpots;
   }

   public boolean isSafe(BlockPos pos) {
      boolean isSafe = true;

      for(BlockPos offset : surroundOffset) {
         Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
         if (block != Blocks.BEDROCK) {
            isSafe = false;
            break;
         }
      }

      return isSafe;
   }
}
