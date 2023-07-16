//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.player;

import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.InventoryUtil;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Anti32k extends Module {
   private static Anti32k INSTANCE = new Anti32k();
   private Setting<Integer> range = this.register(new Setting<>("Range", 5, 3, 5));

   public Anti32k() {
      super("Anti32k", "Anti32k", Module.Category.PLAYER, true, false, false);
      this.setInstance();
   }

   public static Anti32k getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Anti32k();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @Override
   public void onTick() {
      if (!fullNullCheck()) {
         int mainSlot = mc.player.inventory.currentItem;
         if (MoonBreak.getInstance().isOff()) {
            MoonBreak.getInstance().enable();
         }

         for(BlockPos blockPos : this.breakPos(this.range.getValue().floatValue())) {
            int slotPick = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
            if (slotPick == -1) {
               return;
            }

            if (blockPos != null) {
               if (mc.world.getBlockState(blockPos).getBlock() instanceof BlockHopper
                  && mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() instanceof BlockShulkerBox) {
                  mc.player.inventory.currentItem = slotPick;
                  mc.player.swingArm(EnumHand.MAIN_HAND);
                  mc.playerController
                     .processRightClickBlock(
                        mc.player,
                        mc.world,
                        blockPos.add(0, 1, 0),
                        BlockUtil.getRayTraceFacing(blockPos.add(0, 1, 0)),
                        new Vec3d(blockPos.add(0, 1, 0)),
                        EnumHand.MAIN_HAND
                     );
                  mc.playerController.onPlayerDamageBlock(blockPos, BlockUtil.getRayTraceFacing(blockPos));
               } else {
                  mc.player.inventory.currentItem = mainSlot;
               }
            }
         }
      }
   }

   private NonNullList<BlockPos> breakPos(float placeRange) {
      NonNullList<BlockPos> positions = NonNullList.create();
      positions.addAll(
         BlockUtil.getSphere(
            new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)),
            placeRange,
            0,
            false,
            true,
            0
         )
      );
      return positions;
   }
}
