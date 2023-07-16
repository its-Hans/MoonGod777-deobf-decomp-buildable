//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.moon.Moon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class BlockUtil implements Util {
   public static boolean armorTarget;
   public static final List<Block> blackList = Arrays.asList(
      Blocks.ENDER_CHEST,
      Blocks.CHEST,
      Blocks.TRAPPED_CHEST,
      Blocks.CRAFTING_TABLE,
      Blocks.ANVIL,
      Blocks.BREWING_STAND,
      Blocks.HOPPER,
      Blocks.DROPPER,
      Blocks.DISPENSER,
      Blocks.TRAPDOOR,
      Blocks.ENCHANTING_TABLE
   );
   public static final List<Block> shulkerList = Arrays.asList(
      Blocks.WHITE_SHULKER_BOX,
      Blocks.ORANGE_SHULKER_BOX,
      Blocks.MAGENTA_SHULKER_BOX,
      Blocks.LIGHT_BLUE_SHULKER_BOX,
      Blocks.YELLOW_SHULKER_BOX,
      Blocks.LIME_SHULKER_BOX,
      Blocks.PINK_SHULKER_BOX,
      Blocks.GRAY_SHULKER_BOX,
      Blocks.SILVER_SHULKER_BOX,
      Blocks.CYAN_SHULKER_BOX,
      Blocks.PURPLE_SHULKER_BOX,
      Blocks.BLUE_SHULKER_BOX,
      Blocks.BROWN_SHULKER_BOX,
      Blocks.GREEN_SHULKER_BOX,
      Blocks.RED_SHULKER_BOX,
      Blocks.BLACK_SHULKER_BOX
   );
   public static final List<Block> unSafeBlocks = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);
   public static List<Block> unSolidBlocks = Arrays.asList(
      Blocks.FLOWING_LAVA,
      Blocks.FLOWER_POT,
      Blocks.SNOW,
      Blocks.CARPET,
      Blocks.END_ROD,
      Blocks.SKULL,
      Blocks.FLOWER_POT,
      Blocks.TRIPWIRE,
      Blocks.TRIPWIRE_HOOK,
      Blocks.WOODEN_BUTTON,
      Blocks.LEVER,
      Blocks.STONE_BUTTON,
      Blocks.LADDER,
      Blocks.UNPOWERED_COMPARATOR,
      Blocks.POWERED_COMPARATOR,
      Blocks.UNPOWERED_REPEATER,
      Blocks.POWERED_REPEATER,
      Blocks.UNLIT_REDSTONE_TORCH,
      Blocks.REDSTONE_TORCH,
      Blocks.REDSTONE_WIRE,
      Blocks.AIR,
      Blocks.PORTAL,
      Blocks.END_PORTAL,
      Blocks.WATER,
      Blocks.FLOWING_WATER,
      Blocks.LAVA,
      Blocks.FLOWING_LAVA,
      Blocks.SAPLING,
      Blocks.RED_FLOWER,
      Blocks.YELLOW_FLOWER,
      Blocks.BROWN_MUSHROOM,
      Blocks.RED_MUSHROOM,
      Blocks.WHEAT,
      Blocks.CARROTS,
      Blocks.POTATOES,
      Blocks.BEETROOTS,
      Blocks.REEDS,
      Blocks.PUMPKIN_STEM,
      Blocks.MELON_STEM,
      Blocks.WATERLILY,
      Blocks.NETHER_WART,
      Blocks.COCOA,
      Blocks.CHORUS_FLOWER,
      Blocks.CHORUS_PLANT,
      Blocks.TALLGRASS,
      Blocks.DEADBUSH,
      Blocks.VINE,
      Blocks.FIRE,
      Blocks.RAIL,
      Blocks.ACTIVATOR_RAIL,
      Blocks.DETECTOR_RAIL,
      Blocks.GOLDEN_RAIL,
      Blocks.TORCH
   );

   public static List<EnumFacing> getPossibleSides(BlockPos pos) {
      ArrayList<EnumFacing> facings = new ArrayList();

      for(EnumFacing side : EnumFacing.values()) {
         BlockPos neighbour = pos.offset(side);
         if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)
            && !mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
            facings.add(side);
         }
      }

      return facings;
   }

   public static EnumFacing getFirstFacing(BlockPos pos) {
      Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
      return iterator.hasNext() ? (EnumFacing)iterator.next() : null;
   }

   public static EnumFacing getRayTraceFacing(BlockPos pos) {
      RayTraceResult result = mc.world
         .rayTraceBlocks(
            new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ),
            new Vec3d((double)pos.getX() + 0.5, (double)pos.getX() - 0.5, (double)pos.getX() + 0.5)
         );
      return result != null && result.sideHit != null ? result.sideHit : EnumFacing.UP;
   }

   public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
      return isPositionPlaceable(pos, rayTrace, true);
   }

   public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
      Block block = mc.world.getBlockState(pos).getBlock();
      if (!(block instanceof BlockAir)
         && !(block instanceof BlockLiquid)
         && !(block instanceof BlockTallGrass)
         && !(block instanceof BlockFire)
         && !(block instanceof BlockDeadBush)
         && !(block instanceof BlockSnow)) {
         return 0;
      } else if (!rayTracePlaceCheck(pos, rayTrace, 0.0F)) {
         return -1;
      } else {
         if (entityCheck) {
            for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
               if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                  return 1;
               }
            }
         }

         for(EnumFacing side : getPossibleSides(pos)) {
            if (canBeClicked(pos.offset(side))) {
               return 3;
            }
         }

         return 2;
      }
   }

   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
      if (packet) {
         float f = (float)(vec.x - (double)pos.getX());
         float f1 = (float)(vec.y - (double)pos.getY());
         float f2 = (float)(vec.z - (double)pos.getZ());
         mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
      } else {
         mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
      }

      mc.player.swingArm(EnumHand.MAIN_HAND);
      mc.rightClickDelayTimer = 4;
   }

   public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
      boolean sneaking = false;
      EnumFacing side = getFirstFacing(pos);
      if (side == null) {
         return isSneaking;
      } else {
         BlockPos neighbour = pos.offset(side);
         EnumFacing opposite = side.getOpposite();
         Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
         Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
         if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            mc.player.setSneaking(true);
            sneaking = true;
         }

         if (rotate) {
            RotationUtil.faceVector(hitVec, true);
         }

         rightClickBlock(neighbour, hitVec, hand, opposite, packet);
         mc.player.swingArm(EnumHand.MAIN_HAND);
         mc.rightClickDelayTimer = 4;
         return sneaking || isSneaking;
      }
   }

   public static boolean placeBlockSmartRotate(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
      boolean sneaking = false;
      EnumFacing side = getFirstFacing(pos);
      if (side == null) {
         return isSneaking;
      } else {
         BlockPos neighbour = pos.offset(side);
         EnumFacing opposite = side.getOpposite();
         Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
         Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
         if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            sneaking = true;
         }

         if (rotate) {
            Moon.rotationManager.lookAtVec3d(hitVec);
         }

         rightClickBlock(neighbour, hitVec, hand, opposite, packet);
         return sneaking || isSneaking;
      }
   }

   public static void placeBlockStopSneaking(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
      boolean sneaking = placeBlockSmartRotate(pos, hand, rotate, packet, isSneaking);
      if (!isSneaking && sneaking) {
         mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
      }
   }

   public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
      return new Vec3d[]{
         new Vec3d(vec3d.x, vec3d.y - 1.0, vec3d.z),
         new Vec3d(
            vec3d.x != 0.0 ? vec3d.x * 2.0 : vec3d.x,
            vec3d.y,
            vec3d.x != 0.0 ? vec3d.z : vec3d.z * 2.0
         ),
         new Vec3d(
            vec3d.x == 0.0 ? vec3d.x + 1.0 : vec3d.x,
            vec3d.y,
            vec3d.x == 0.0 ? vec3d.z : vec3d.z + 1.0
         ),
         new Vec3d(
            vec3d.x == 0.0 ? vec3d.x - 1.0 : vec3d.x,
            vec3d.y,
            vec3d.x == 0.0 ? vec3d.z : vec3d.z - 1.0
         ),
         new Vec3d(vec3d.x, vec3d.y + 1.0, vec3d.z)
      };
   }

   public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
      List<BlockPos> circleBlocks = new ArrayList();
      int cx = loc.getX();
      int cy = loc.getY();
      int cz = loc.getZ();

      for(int x = cx - (int)r; (float)x <= (float)cx + r; ++x) {
         for(int z = cz - (int)r; (float)z <= (float)cz + r; ++z) {
            for(int y = sphere ? cy - (int)r : cy; (float)y < (sphere ? (float)cy + r : (float)(cy + h)); ++y) {
               double dist = (double)((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));
               if (dist < (double)(r * r) && (!hollow || !(dist < (double)((r - 1.0F) * (r - 1.0F))))) {
                  BlockPos l = new BlockPos(x, y + plus_y, z);
                  circleBlocks.add(l);
               }
            }
         }
      }

      return circleBlocks;
   }

   public static boolean canPlaceCrystal(BlockPos blockPos) {
      BlockPos boost = blockPos.add(0, 1, 0);
      BlockPos boost2 = blockPos.add(0, 2, 0);

      try {
         if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK
            && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
            return false;
         } else if (mc.world.getBlockState(boost).getBlock() == Blocks.AIR
            && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR) {
            for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
               if (!(entity instanceof EntityEnderCrystal)) {
                  return false;
               }
            }

            for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
               if (!(entity instanceof EntityEnderCrystal)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } catch (Exception var5) {
         return false;
      }
   }

   public static double getDistance32k(Entity player, BlockPos pos) {
      double d0 = player.posX - (double)pos.getX();
      double d1 = player.posY + 1.0 - (double)pos.getY();
      double d2 = player.posZ - (double)pos.getZ();
      return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
   }

   public static boolean canBlockBeSeen(double x, double y, double z) {
      return mc.world
            .rayTraceBlocks(
               new Vec3d(
                  mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ
               ),
               new Vec3d(x, y + 1.7, z),
               false,
               true,
               false
            )
         == null;
   }

   public static boolean canBeClicked(BlockPos pos) {
      return getBlock(pos).canCollideCheck(getState(pos), false);
   }

   private static Block getBlock(BlockPos pos) {
      return getState(pos).getBlock();
   }

   private static IBlockState getState(BlockPos pos) {
      return mc.world.getBlockState(pos);
   }

   public static boolean isBlockAboveEntitySolid(Entity entity) {
      if (entity != null) {
         BlockPos pos = new BlockPos(entity.posX, entity.posY + 2.0, entity.posZ);
         return isBlockSolid(pos);
      } else {
         return false;
      }
   }

   public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand) {
      mc.player
         .connection
         .sendPacket(
            new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, hand, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ())
         );
   }

   public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
      BlockPos[] list = new BlockPos[vec3ds.length];

      for(int i = 0; i < vec3ds.length; ++i) {
         list[i] = new BlockPos(vec3ds[i]);
      }

      return list;
   }

   public static Vec3d posToVec3d(BlockPos pos) {
      return new Vec3d(pos);
   }

   public static BlockPos vec3dToPos(Vec3d vec3d) {
      return new BlockPos(vec3d);
   }

   public static boolean isBlockBelowEntitySolid(Entity entity) {
      if (entity != null) {
         BlockPos pos = new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ);
         return isBlockSolid(pos);
      } else {
         return false;
      }
   }

   public static boolean isBlockSolid(BlockPos pos) {
      return !isBlockUnSolid(pos);
   }

   public static boolean isBlockUnSolid(BlockPos pos) {
      return isBlockUnSolid(mc.world.getBlockState(pos).getBlock());
   }

   public static boolean isBlockUnSolid(Block block) {
      return unSolidBlocks.contains(block);
   }

   public static boolean isBlockUnSafe(Block block) {
      return unSafeBlocks.contains(block);
   }

   public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
      Vec3d[] output = new Vec3d[input.length];

      for(int i = 0; i < input.length; ++i) {
         output[i] = vec3d.add(input[i]);
      }

      return output;
   }

   public static Boolean isPosInFov(BlockPos pos) {
      int dirnumber = RotationUtil.getDirection4D();
      if (dirnumber == 0 && (double)pos.getZ() - mc.player.getPositionVector().z < 0.0) {
         return false;
      } else if (dirnumber == 1 && (double)pos.getX() - mc.player.getPositionVector().x > 0.0) {
         return false;
      } else {
         return dirnumber == 2 && (double)pos.getZ() - mc.player.getPositionVector().z > 0.0
            ? false
            : dirnumber != 3 || (double)pos.getX() - mc.player.getPositionVector().x >= 0.0;
      }
   }

   public static boolean canBreak(BlockPos pos) {
      IBlockState blockState = mc.world.getBlockState(pos);
      Block block = blockState.getBlock();
      return block.getBlockHardness(blockState, mc.world, pos) != -1.0F;
   }

   public static boolean isValidBlock(BlockPos pos) {
      Block block = mc.world.getBlockState(pos).getBlock();
      return !(block instanceof BlockLiquid) && block.getMaterial(null) != Material.AIR;
   }

   public static boolean isScaffoldPos(BlockPos pos) {
      return mc.world.isAirBlock(pos)
         || mc.world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER
         || mc.world.getBlockState(pos).getBlock() == Blocks.TALLGRASS
         || mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid;
   }

   public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
      return !shouldCheck
         || mc.world
               .rayTraceBlocks(
                  new Vec3d(
                     mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ
                  ),
                  new Vec3d((double)pos.getX(), (double)((float)pos.getY() + height), (double)pos.getZ()),
                  false,
                  true,
                  false
               )
            == null;
   }

   public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
      return rayTracePlaceCheck(pos, shouldCheck, 1.0F);
   }
}
