//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.moon.Moon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtil implements Util {
   public static final Vec3d[] antiDropOffsetList = new Vec3d[]{new Vec3d(0.0, -2.0, 0.0)};
   public static final Vec3d[] platformOffsetList = new Vec3d[]{
      new Vec3d(0.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(1.0, -1.0, 0.0)
   };
   public static final Vec3d[] legOffsetList = new Vec3d[]{
      new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0)
   };
   public static final Vec3d[] OffsetList = new Vec3d[]{
      new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, 0.0)
   };
   public static final Vec3d[] antiStepOffsetList = new Vec3d[]{
      new Vec3d(-1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 2.0, -1.0)
   };
   public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[]{new Vec3d(0.0, 3.0, 0.0)};

   public static void attackEntity(Entity entity, boolean packet, EnumHand hand) {
      if (packet) {
         mc.player.connection.sendPacket(new CPacketUseEntity(entity));
      } else {
         mc.playerController.attackEntity(mc.player, entity);
      }

      mc.player.swingArm(hand);
   }

   public static Vec3d interpolateEntity(Entity entity, float time) {
      return new Vec3d(
         entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time,
         entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time,
         entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time
      );
   }

   public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
      return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, partialTicks));
   }

   public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
      return getInterpolatedPos(entity, partialTicks)
         .subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
   }

   public static Vec3d getInterpolatedRenderPos(Vec3d vec) {
      return new Vec3d(vec.x, vec.y, vec.z)
         .subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
      return new Vec3d(
         (entity.posX - entity.lastTickPosX) * x,
         (entity.posY - entity.lastTickPosY) * y,
         (entity.posZ - entity.lastTickPosZ) * z
      );
   }

   public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
      return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
      return getInterpolatedAmount(entity, (double)partialTicks, (double)partialTicks, (double)partialTicks);
   }

   public static boolean isPassive(Entity entity) {
      if (entity instanceof EntityWolf && ((EntityWolf)entity).isAngry()) {
         return false;
      } else if (!(entity instanceof EntityAgeable) && !(entity instanceof EntityAmbientCreature) && !(entity instanceof EntitySquid)) {
         return entity instanceof EntityIronGolem && ((EntityIronGolem)entity).getRevengeTarget() == null;
      } else {
         return true;
      }
   }

   public static boolean isSafe(Entity entity, int height, boolean floor) {
      return getUnsafeBlocks(entity, height, floor).size() == 0;
   }

   public static boolean stopSneaking(boolean isSneaking) {
      if (isSneaking && mc.player != null) {
         mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
      }

      return false;
   }

   public static BlockPos getPlayerPos(EntityPlayer player) {
      return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
   }

   public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
      return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
   }

   public static boolean isMobAggressive(Entity entity) {
      if (entity instanceof EntityPigZombie) {
         if (((EntityPigZombie)entity).isArmsRaised() || ((EntityPigZombie)entity).isAngry()) {
            return true;
         }
      } else {
         if (entity instanceof EntityWolf) {
            return ((EntityWolf)entity).isAngry() && !mc.player.equals(((EntityWolf)entity).getOwner());
         }

         if (entity instanceof EntityEnderman) {
            return ((EntityEnderman)entity).isScreaming();
         }
      }

      return isHostileMob(entity);
   }

   public static boolean isNeutralMob(Entity entity) {
      return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
   }

   public static boolean isProjectile(Entity entity) {
      return entity instanceof EntityShulkerBullet || entity instanceof EntityFireball;
   }

   public static boolean isVehicle(Entity entity) {
      return entity instanceof EntityBoat || entity instanceof EntityMinecart;
   }

   public static boolean isFriendlyMob(Entity entity) {
      return entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity)
         || entity.isCreatureType(EnumCreatureType.AMBIENT, false)
         || entity instanceof EntityVillager
         || entity instanceof EntityIronGolem
         || isNeutralMob(entity) && !isMobAggressive(entity);
   }

   public static boolean isHostileMob(Entity entity) {
      return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
   }

   public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
      ArrayList<Vec3d> vec3d = new ArrayList();

      for(Vec3d vector : getOffsets(height, floor)) {
         BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
         Block block = mc.world.getBlockState(targetPos).getBlock();
         if (block instanceof BlockAir
            || block instanceof BlockLiquid
            || block instanceof BlockTallGrass
            || block instanceof BlockFire
            || block instanceof BlockDeadBush
            || block instanceof BlockSnow) {
            vec3d.add(vector);
         }
      }

      return vec3d;
   }

   public static boolean getSurroundWeakness(final Vec3d pos, final int feetMine, final int render) {
      switch (feetMine) {
         case 1: {
            final BlockPos raytrace = new BlockPos(pos);
            if (!BlockUtil.canBlockBeSeen(raytrace.getX() - 2, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)(raytrace.getX() - 2), (double)raytrace.getY(), (double)raytrace.getZ())) > 3.0) {
               return false;
            }
            final Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 1, 0)).getBlock();
            if (block == Blocks.AIR || block == Blocks.FIRE) {
               final Block blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 0, 0)).getBlock();
               if (blocka == Blocks.AIR || blocka == Blocks.FIRE) {
                  final Block blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, -1, 0)).getBlock();
                  if ((blockb == Blocks.OBSIDIAN || blockb == Blocks.BEDROCK) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                     return true;
                  }
               }
            }
            break;
         }
         case 2: {
            final BlockPos raytrace = new BlockPos(pos);
            if (!BlockUtil.canBlockBeSeen(raytrace.getX() + 2, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)(raytrace.getX() + 2), (double)raytrace.getY(), (double)raytrace.getZ())) > 3.0) {
               return false;
            }
            final Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 1, 0)).getBlock();
            if (block == Blocks.AIR || block == Blocks.FIRE) {
               final Block blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 0, 0)).getBlock();
               if (blocka == Blocks.AIR || blocka == Blocks.FIRE) {
                  final Block blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, -1, 0)).getBlock();
                  if ((blockb == Blocks.OBSIDIAN || blockb == Blocks.BEDROCK) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                     return true;
                  }
               }
            }
            break;
         }
         case 3: {
            final BlockPos raytrace = new BlockPos(pos);
            if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() - 2) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)raytrace.getX(), (double)raytrace.getY(), (double)(raytrace.getZ() - 2))) > 3.0) {
               return false;
            }
            final Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -2)).getBlock();
            if (block == Blocks.AIR || block == Blocks.FIRE) {
               final Block blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -2)).getBlock();
               if (blocka == Blocks.AIR || blocka == Blocks.FIRE) {
                  final Block blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, -2)).getBlock();
                  if ((blockb == Blocks.OBSIDIAN || blockb == Blocks.BEDROCK) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                     return true;
                  }
               }
            }
            break;
         }
         case 4: {
            final BlockPos raytrace = new BlockPos(pos);
            if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() + 2) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)raytrace.getX(), (double)raytrace.getY(), (double)(raytrace.getZ() + 2))) > 3.0) {
               return false;
            }
            final Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 2)).getBlock();
            if (block == Blocks.AIR || block == Blocks.FIRE) {
               final Block blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 2)).getBlock();
               if (blocka == Blocks.AIR || blocka == Blocks.FIRE) {
                  final Block blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, 2)).getBlock();
                  if ((blockb == Blocks.OBSIDIAN || blockb == Blocks.BEDROCK) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                     return true;
                  }
               }
            }
            break;
         }
         case 5: {
            final BlockPos raytrace = new BlockPos(pos);
            if (!BlockUtil.canBlockBeSeen(raytrace.getX() - 1, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)(raytrace.getX() - 1), (double)raytrace.getY(), (double)raytrace.getZ())) > 3.0) {
               return false;
            }
            final Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 1, 0)).getBlock();
            if ((block == Blocks.AIR || block == Blocks.FIRE) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
               return true;
            }
            break;
         }
         case 6: {
            final BlockPos raytrace = new BlockPos(pos);
            if (!BlockUtil.canBlockBeSeen(raytrace.getX() + 1, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)(raytrace.getX() + 1), (double)raytrace.getY(), (double)raytrace.getZ())) > 3.0) {
               return false;
            }
            final Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 1, 0)).getBlock();
            if ((block == Blocks.AIR || block == Blocks.FIRE) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
               return true;
            }
            break;
         }
         case 7: {
            final BlockPos raytrace = new BlockPos(pos);
            if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() - 1) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)raytrace.getX(), (double)raytrace.getY(), (double)(raytrace.getZ() - 1))) > 3.0) {
               return false;
            }
            final Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -1)).getBlock();
            if ((block == Blocks.AIR || block == Blocks.FIRE) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
               return true;
            }
            break;
         }
         case 8: {
            final BlockPos raytrace = new BlockPos(pos);
            if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() + 1) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)raytrace.getX(), (double)raytrace.getY(), (double)(raytrace.getZ() + 1))) > 3.0) {
               return false;
            }
            final Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 1)).getBlock();
            if ((block == Blocks.AIR || block == Blocks.FIRE) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
               return true;
            }
            break;
         }
      }
      switch (render) {
         case 1: {
            final Block block2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 1, 0)).getBlock();
            if (block2 == Blocks.AIR || block2 == Blocks.FIRE) {
               final Block blocka2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 0, 0)).getBlock();
               if (blocka2 == Blocks.AIR || blocka2 == Blocks.FIRE) {
                  final Block blockb2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, -1, 0)).getBlock();
                  if ((blockb2 == Blocks.OBSIDIAN || blockb2 == Blocks.BEDROCK) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                     return true;
                  }
               }
            }
            break;
         }
         case 2: {
            final Block block2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 1, 0)).getBlock();
            if (block2 == Blocks.AIR || block2 == Blocks.FIRE) {
               final Block blocka2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 0, 0)).getBlock();
               if (blocka2 == Blocks.AIR || blocka2 == Blocks.FIRE) {
                  final Block blockb2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, -1, 0)).getBlock();
                  if ((blockb2 == Blocks.OBSIDIAN || blockb2 == Blocks.BEDROCK) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                     return true;
                  }
               }
            }
            break;
         }
         case 3: {
            final Block block2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -2)).getBlock();
            if (block2 == Blocks.AIR || block2 == Blocks.FIRE) {
               final Block blocka2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -2)).getBlock();
               if (blocka2 == Blocks.AIR || blocka2 == Blocks.FIRE) {
                  final Block blockb2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, -2)).getBlock();
                  if ((blockb2 == Blocks.OBSIDIAN || blockb2 == Blocks.BEDROCK) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                     return true;
                  }
               }
            }
            break;
         }
         case 4: {
            final Block block2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 2)).getBlock();
            if (block2 == Blocks.AIR || block2 == Blocks.FIRE) {
               final Block blocka2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 2)).getBlock();
               if (blocka2 == Blocks.AIR || blocka2 == Blocks.FIRE) {
                  final Block blockb2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, 2)).getBlock();
                  if ((blockb2 == Blocks.OBSIDIAN || blockb2 == Blocks.BEDROCK) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                     return true;
                  }
               }
            }
            break;
         }
         case 5: {
            final Block block2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 1, 0)).getBlock();
            if ((block2 == Blocks.AIR || block2 == Blocks.FIRE) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
               return true;
            }
            break;
         }
         case 6: {
            final Block block2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 1, 0)).getBlock();
            if ((block2 == Blocks.AIR || block2 == Blocks.FIRE) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
               return true;
            }
            break;
         }
         case 7: {
            final Block block2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -1)).getBlock();
            if ((block2 == Blocks.AIR || block2 == Blocks.FIRE) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
               return true;
            }
            break;
         }
         case 8: {
            final Block block2 = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 1)).getBlock();
            if ((block2 == Blocks.AIR || block2 == Blocks.FIRE) && EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
               return true;
            }
            break;
         }
      }
      return false;
   }

   public static boolean isInHole(Entity entity) {
      return isBedrockHole(new BlockPos(entity.posX, entity.posY, entity.posZ))
         || isObbyHole(new BlockPos(entity.posX, entity.posY, entity.posZ))
         || isBothHole(new BlockPos(entity.posX, entity.posY, entity.posZ));
   }

   public static boolean isObbyHole(BlockPos blockPos) {
      for(BlockPos pos : new BlockPos[]{
         blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()
      }) {
         IBlockState touchingState = mc.world.getBlockState(pos);
         if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.OBSIDIAN) {
            return false;
         }
      }

      return true;
   }

   public static boolean isBedrockHole(BlockPos blockPos) {
      for(BlockPos pos : new BlockPos[]{
         blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()
      }) {
         IBlockState touchingState = mc.world.getBlockState(pos);
         if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK) {
            return false;
         }
      }

      return true;
   }

   public static boolean isBothHole(BlockPos blockPos) {
      for(BlockPos pos : new BlockPos[]{
         blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()
      }) {
         IBlockState touchingState = mc.world.getBlockState(pos);
         if (touchingState.getBlock() == Blocks.AIR
            || touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN) {
            return false;
         }
      }

      return true;
   }

   public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
      List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
      Vec3d[] array = new Vec3d[list.size()];
      return list.toArray(array);
   }

   public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
      List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor);
      Vec3d[] array = new Vec3d[list.size()];
      return list.toArray(array);
   }

   public static double getDst(Vec3d vec) {
      return mc.player.getPositionVector().distanceTo(vec);
   }

   public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
   }

   public static boolean isTrappedExtended(
      int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace
   ) {
      return getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace).size() == 0;
   }

   public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      ArrayList<Vec3d> vec3ds = new ArrayList();
      if (!antiStep && getUnsafeBlocks(player, 2, false).size() == 4) {
         vec3ds.addAll(getUnsafeBlocks(player, 2, false));
      }

      for(int i = 0; i < getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop).length; ++i) {
         Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)[i];
         BlockPos targetPos = new BlockPos(player.getPositionVector()).add(vector.x, vector.y, vector.z);
         Block block = mc.world.getBlockState(targetPos).getBlock();
         if (block instanceof BlockAir
            || block instanceof BlockLiquid
            || block instanceof BlockTallGrass
            || block instanceof BlockFire
            || block instanceof BlockDeadBush
            || block instanceof BlockSnow) {
            vec3ds.add(vector);
         }
      }

      return vec3ds;
   }

   public static boolean isInWater(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         double y = entity.posY + 0.01;

         for(int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
            for(int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
               BlockPos pos = new BlockPos(x, (int)y, z);
               if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static boolean isDrivenByPlayer(Entity entityIn) {
      return mc.player != null && entityIn != null && entityIn.equals(mc.player.getRidingEntity());
   }

   public static boolean isPlayer(Entity entity) {
      return entity instanceof EntityPlayer;
   }

   public static boolean isAboveWater(Entity entity) {
      return isAboveWater(entity, false);
   }

   public static boolean isAboveWater(Entity entity, boolean packet) {
      if (entity == null) {
         return false;
      } else {
         double y = entity.posY - (packet ? 0.03 : (isPlayer(entity) ? 0.2 : 0.5));

         for(int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
            for(int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
               BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
               if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static List<Vec3d> getUntrappedBlocksExtended(
      int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace
   ) {
      ArrayList<Vec3d> placeTargets = new ArrayList();
      if (extension == 1) {
         placeTargets.addAll(targets(player.getPositionVector(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
      } else {
         int extend = 1;

         for(Vec3d vec3d : MathUtil.getBlockBlocks(player)) {
            if (extend > extension) {
               break;
            }

            placeTargets.addAll(targets(vec3d, antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
            ++extend;
         }
      }

      ArrayList<Vec3d> removeList = new ArrayList();

      for(Vec3d vec3d : placeTargets) {
         BlockPos pos = new BlockPos(vec3d);
         if (BlockUtil.isPositionPlaceable(pos, raytrace) == -1) {
            removeList.add(vec3d);
         }
      }

      for(Vec3d vec3d : removeList) {
         placeTargets.remove(vec3d);
      }

      return placeTargets;
   }

   public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
      ArrayList<Vec3d> placeTargets = new ArrayList();
      if (antiDrop) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList));
      }

      if (platform) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
      }

      if (legs) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
      }

      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
      if (antiStep) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
      } else {
         List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
         if (vec3ds.size() == 4) {
            for(Vec3d vector : vec3ds) {
               BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
               switch(BlockUtil.isPositionPlaceable(position, raytrace)) {
                  case -1:
                  case 1:
                  case 2:
                     break;
                  case 3:
                     placeTargets.add(vec3d.add(vector));
                  case 0:
                  default:
                     if (antiScaffold) {
                        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
                     }

                     return placeTargets;
               }
            }
         }
      }

      if (antiScaffold) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
      }

      return placeTargets;
   }

   public static List<Vec3d> getOffsetList(int y, boolean floor) {
      ArrayList<Vec3d> offsets = new ArrayList();
      offsets.add(new Vec3d(-1.0, (double)y, 0.0));
      offsets.add(new Vec3d(1.0, (double)y, 0.0));
      offsets.add(new Vec3d(0.0, (double)y, -1.0));
      offsets.add(new Vec3d(0.0, (double)y, 1.0));
      if (floor) {
         offsets.add(new Vec3d(0.0, (double)(y - 1), 0.0));
      }

      return offsets;
   }

   public static Vec3d[] getOffsets(int y, boolean floor) {
      List<Vec3d> offsets = getOffsetList(y, floor);
      Vec3d[] array = new Vec3d[offsets.size()];
      return offsets.toArray(array);
   }

   public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
      Vec3d[] array = new Vec3d[offsets.size()];
      return offsets.toArray(array);
   }

   public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      ArrayList<Vec3d> offsets = new ArrayList(getOffsetList(1, false));
      offsets.add(new Vec3d(0.0, 2.0, 0.0));
      if (antiScaffold) {
         offsets.add(new Vec3d(0.0, 3.0, 0.0));
      }

      if (antiStep) {
         offsets.addAll(getOffsetList(2, false));
      }

      if (legs) {
         offsets.addAll(getOffsetList(0, false));
      }

      if (platform) {
         offsets.addAll(getOffsetList(-1, false));
         offsets.add(new Vec3d(0.0, -1.0, 0.0));
      }

      if (antiDrop) {
         offsets.add(new Vec3d(0.0, -2.0, 0.0));
      }

      return offsets;
   }

   public static Vec3d[] getHeightOffsets(int min, int max) {
      ArrayList<Vec3d> offsets = new ArrayList();

      for(int i = min; i <= max; ++i) {
         offsets.add(new Vec3d(0.0, (double)i, 0.0));
      }

      Vec3d[] array = new Vec3d[offsets.size()];
      return offsets.toArray(array);
   }

   public static BlockPos getRoundedBlockPos(Entity entity) {
      return new BlockPos(MathUtil.roundVec(entity.getPositionVector(), 0));
   }

   public static boolean isLiving(Entity entity) {
      return entity instanceof EntityLivingBase;
   }

   public static boolean isAlive(Entity entity) {
      return isLiving(entity) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0F;
   }

   public static boolean isDead(Entity entity) {
      return !isAlive(entity);
   }

   public static boolean isntValid(Entity entity, double range) {
      return entity == null
         || isDead(entity)
         || entity.equals(mc.player)
         || entity instanceof EntityPlayer && Moon.friendManager.isFriend(entity.getName())
         || mc.player.getDistanceSq(entity) > MathUtil.square(range);
   }

   public static boolean holdingWeapon(EntityPlayer player) {
      return player.getHeldItemMainhand().getItem() instanceof ItemSword || player.getHeldItemMainhand().getItem() instanceof ItemAxe;
   }

   public static boolean rayTraceHitCheck(Entity entity, boolean shouldCheck) {
      return !shouldCheck || mc.player.canEntityBeSeen(entity);
   }

   public static boolean isMoving() {
      return (double)mc.player.moveForward != 0.0 || (double)mc.player.moveStrafing != 0.0;
   }

   public static BlockPos getPlayerPosWithEntity() {
      return new BlockPos(
         mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().posX : mc.player.posX,
         mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().posY : mc.player.posY,
         mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().posZ : mc.player.posZ
      );
   }

   public static Map<String, Integer> getTextRadarPlayers() {
      Map<String, Integer> output = new HashMap<>();
      DecimalFormat dfHealth = new DecimalFormat("#.#");
      dfHealth.setRoundingMode(RoundingMode.CEILING);
      DecimalFormat dfDistance = new DecimalFormat("#.#");
      dfDistance.setRoundingMode(RoundingMode.CEILING);
      StringBuilder healthSB = new StringBuilder();
      StringBuilder distanceSB = new StringBuilder();

      for(EntityPlayer player : mc.world.playerEntities) {
         if (!player.isInvisible() && !player.getName().equals(mc.player.getName())) {
            int hpRaw = (int)((float)((int)player.getHealth()) + player.getAbsorptionAmount());
            String hp = dfHealth.format((long)hpRaw);
            healthSB.append("Â§");
            if (hpRaw >= 20) {
               healthSB.append("a");
            } else if (hpRaw >= 10) {
               healthSB.append("e");
            } else if (hpRaw >= 5) {
               healthSB.append("6");
            } else {
               healthSB.append("c");
            }

            healthSB.append(hp);
            int distanceInt = (int)mc.player.getDistance(player);
            String distance = dfDistance.format((long)distanceInt);
            distanceSB.append("Â§");
            if (distanceInt >= 25) {
               distanceSB.append("a");
            } else if (distanceInt > 10) {
               distanceSB.append("6");
            } else {
               distanceSB.append("c");
            }

            distanceSB.append(distance);
            output.put(
               healthSB.toString()
                  + " "
                  + (Moon.friendManager.isFriend(player) ? ChatFormatting.AQUA : ChatFormatting.RED)
                  + player.getName()
                  + " "
                  + distanceSB.toString()
                  + " Â§f0",
               (int)mc.player.getDistance(player)
            );
            healthSB.setLength(0);
            distanceSB.setLength(0);
         }
      }

      if (!output.isEmpty()) {
         output = MathUtil.sortByValue(output, false);
      }

      return output;
   }

   public static boolean isAboveBlock(Entity entity, BlockPos blockPos) {
      return entity.posY >= (double)blockPos.getY();
   }

   public static void setTimer(float speed) {
      mc.timer.tickLength = 50.0F / speed;
   }

   public static void resetTimer() {
      mc.timer.tickLength = 50.0F;
   }
}
