//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.moon.Moon;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import me.moon.features.modules.movement.NoFall;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.EntityUtil;
import me.moon.util.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class Burrow extends Module {
   private final Setting<Boolean> center = this.register(new Setting<>("TPCenter", false));
   private Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
   private boolean isSneaking = false;
   private BlockPos startPos = null;
   private boolean noFall = false;

   public Burrow() {
      super("Burrow", "XD", Module.Category.PLAYER, true, false, false);
   }

   @Override
   public void onDisable() {
      this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
      if (this.noFall) {
         NoFall.getInstance().enable();
         this.noFall = false;
      }
   }

   @Override
   public void onEnable() {
      if (NoFall.getInstance().isOn()) {
         NoFall.getInstance().disable();
         this.noFall = true;
      }

      this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
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

      if (InventoryUtil.findHotbarBlock(BlockObsidian.class) == -1) {
         Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "Obsidian ?");
         this.disable();
      } else if (mc.world.getBlockState(new BlockPos(mc.player.getPositionVector().add(0.0, 3.0, 0.0))).getBlock()
            == Blocks.AIR
         && mc.world.getBlockState(new BlockPos(mc.player.getPositionVector().add(0.0, 2.0, 0.0))).getBlock()
            == Blocks.AIR) {
         int a = mc.player.inventory.currentItem;
         mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(BlockObsidian.class);
         mc.playerController.updateController();
         mc.player
            .connection
            .sendPacket(
               new Position(mc.player.posX, mc.player.posY + 0.41999998688698, mc.player.posZ, true)
            );
         mc.player
            .connection
            .sendPacket(
               new Position(mc.player.posX, mc.player.posY + 0.7531999805211997, mc.player.posZ, true)
            );
         mc.player
            .connection
            .sendPacket(
               new Position(mc.player.posX, mc.player.posY + 1.00133597911214, mc.player.posZ, true)
            );
         mc.player
            .connection
            .sendPacket(
               new Position(mc.player.posX, mc.player.posY + 1.16610926093821, mc.player.posZ, true)
            );
         this.isSneaking = BlockUtil.placeBlock(
            new BlockPos(EntityUtil.getPlayerPos(mc.player)), EnumHand.MAIN_HAND, this.rotate.getValue(), true, this.isSneaking
         );
         mc.player.inventory.currentItem = a;
         mc.playerController.updateController();
         mc.player
            .connection
            .sendPacket(new Position(mc.player.posX, mc.player.posY + 3.0, mc.player.posZ, false));
         this.disable();
      } else {
         Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "From above stop you !");
         this.disable();
      }
   }
}
