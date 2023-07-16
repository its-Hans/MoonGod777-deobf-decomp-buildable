//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import me.moon.features.modules.player.Anti32k;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.EntityUtil;
import me.moon.util.InventoryUtil;
import me.moon.util.TestUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Auto32k extends Module {
   private static Auto32k INSTANCE = new Auto32k();
   private Setting<Integer> itemSlot = this.register(new Setting<>("ItemSlot", 8, 0, 8));
   public Setting<Boolean> lookMode = this.register(new Setting<>("LookMode", false));
   public Setting<Boolean> smartHit$XIN = this.register(new Setting<>("SmartHit-XIN", false));
   public BlockPos placePos;

   public Auto32k() {
      super("Auto32k", "404 is God", Module.Category.COMBAT, true, false, false);
      this.setInstance();
   }

   public static Auto32k getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Auto32k();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @Override
   public void onUpdate() {
      if (!fullNullCheck()) {
         if (Anti32k.getInstance().isOn()) {
            Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "Oh no Anti32k");
            this.disable();
         } else {
            if (Killaura.getInstance().isOff()) {
               Killaura.getInstance().enable();
            }

            if (!this.lookMode.getValue() && Killaura.getInstance().isOn() && Killaura.getInstance().target != null) {
               for(int x = -5; x <= 5; ++x) {
                  for(int y = -3; y < 0; ++y) {
                     for(int z = -5; z <= 5; ++z) {
                        BlockPos autoPos = EntityUtil.getPlayerPos(mc.player).add(x, y, z);
                        if (!TestUtil.emptyBlocks.contains(mc.world.getBlockState(autoPos).getBlock())
                           && TestUtil.emptyBlocks.contains(mc.world.getBlockState(autoPos.add(0, 1, 0)).getBlock())
                           && TestUtil.emptyBlocks.contains(mc.world.getBlockState(autoPos.add(0, 2, 0)).getBlock())
                           && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(autoPos.add(0, 1, 0))).isEmpty()
                           && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(autoPos.add(0, 2, 0))).isEmpty()
                           && !(BlockUtil.getDistance32k(Killaura.getInstance().target, autoPos.add(0, 1, 0)) <= 6.0)
                           && !(BlockUtil.getDistance32k(Killaura.getInstance().target, autoPos.add(0, 2, 0)) <= 6.0)
                           && !(BlockUtil.getDistance32k(mc.player, autoPos) > 4.5)
                           && !(BlockUtil.getDistance32k(mc.player, autoPos.add(0, 1, 0)) > 4.5)
                           && !(BlockUtil.getDistance32k(mc.player, autoPos.add(0, 2, 0)) > 4.5)) {
                           this.placePos = autoPos;
                        }
                     }
                  }
               }
            }

            if (mc.currentScreen instanceof GuiHopper) {
               int swordIndex = -1;

               for(int i = 0; i < 5; ++i) {
                  if (((Slot)mc.player.openContainer.inventorySlots.get(0)).inventory.getStackInSlot(i).getItem() == Items.DIAMOND_SWORD) {
                     swordIndex = i;
                     break;
                  }
               }

               if (swordIndex == -1) {
                  return;
               }

               if (mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_SWORD) {
                  GuiHopper gui = (GuiHopper)mc.currentScreen;
                  mc.playerController.windowClick(gui.inventorySlots.windowId, swordIndex, this.itemSlot.getValue(), ClickType.SWAP, mc.player);
                  mc.player.inventory.currentItem = this.itemSlot.getValue();
               }
            }
         }
      }
   }

   @Override
   public void onEnable() {
      if (!fullNullCheck()) {
         if (InventoryUtil.findHotbarBlock(BlockHopper.class) != -1 && InventoryUtil.findHotbarBlock(BlockShulkerBox.class) != -1) {
            if (this.lookMode.getValue()) {
               BlockPos rayPos = mc.player.rayTrace(4.5, mc.getRenderPartialTicks()).getBlockPos();
               if (TestUtil.emptyBlocks.contains(mc.world.getBlockState(rayPos).getBlock())
                  || !TestUtil.emptyBlocks.contains(mc.world.getBlockState(rayPos.add(0, 1, 0)).getBlock())
                  || !TestUtil.emptyBlocks.contains(mc.world.getBlockState(rayPos.add(0, 2, 0)).getBlock())
                  || !mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(rayPos.add(0, 1, 0))).isEmpty()
                  || !mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(rayPos.add(0, 2, 0))).isEmpty()) {
                  return;
               }

               this.placePos = rayPos;
            }

            if (this.placePos != null) {
               this.placeBlock(this.placePos.add(0, 1, 0), InventoryUtil.findHotbarBlock(BlockHopper.class));
               this.placeBlock(this.placePos.add(0, 2, 0), InventoryUtil.findHotbarBlock(BlockShulkerBox.class));
               this.openBlock(this.placePos.add(0, 1, 0));
               this.placePos = null;
            }
         }
      }
   }

   private void placeBlock(BlockPos pos, int slot) {
      if (TestUtil.emptyBlocks.contains(mc.world.getBlockState(pos).getBlock())) {
         if (slot != mc.player.inventory.currentItem) {
            mc.player.inventory.currentItem = slot;
         }

         for(EnumFacing f : EnumFacing.values()) {
            Block neighborBlock = mc.world.getBlockState(pos.offset(f)).getBlock();
            if (!TestUtil.emptyBlocks.contains(neighborBlock)) {
               mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
               mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
               mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
            }
         }
      }
   }

   private void openBlock(BlockPos pos) {
      for(EnumFacing f : EnumFacing.values()) {
         Block neighborBlock = mc.world.getBlockState(pos.offset(f)).getBlock();
         if (TestUtil.emptyBlocks.contains(neighborBlock)) {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
         }
      }
   }
}
