//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.player;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import me.moon.event.events.PacketEvent;
import me.moon.event.events.PlayerDamageBlockEvent;
import me.moon.event.events.Render3DEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.InventoryUtil;
import me.moon.util.RenderUtil;
import me.moon.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MoonBreak extends Module {
   private final Timer breakSuccess = new Timer();
   private static MoonBreak INSTANCE = new MoonBreak();
   private Setting<Boolean> creativeMode = this.register(new Setting<>("CreativeMode", true));
   private Setting<Boolean> ghostHand = this.register(new Setting<>("GhostHand", true, v -> this.creativeMode.getValue()));
   private Setting<Boolean> render = this.register(new Setting<>("Render", true));
   private final List<Block> godBlocks = Arrays.asList(
      Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.BEDROCK
   );
   private boolean cancelStart = false;
   private boolean empty = false;
   private EnumFacing facing;
   private BlockPos breakPos;

   public MoonBreak() {
      super("SpeedMine", "MoonBreak", Module.Category.PLAYER, true, false, false);
      this.setInstance();
   }

   public static MoonBreak getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new MoonBreak();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @Override
   public void onUpdate() {
      if (!fullNullCheck()) {
         if (this.creativeMode.getValue() && this.cancelStart && !this.godBlocks.contains(mc.world.getBlockState(this.breakPos).getBlock())) {
            if (this.ghostHand.getValue() && InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1) {
               int slotMain = mc.player.inventory.currentItem;
               if (mc.world.getBlockState(this.breakPos).getBlock() == Blocks.OBSIDIAN) {
                  if (this.breakSuccess.passedMs(1234L)) {
                     mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
                     mc.playerController.updateController();
                     mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.breakPos, this.facing));
                     mc.player.inventory.currentItem = slotMain;
                     mc.playerController.updateController();
                  }
               } else {
                  mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
                  mc.playerController.updateController();
                  mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.breakPos, this.facing));
                  mc.player.inventory.currentItem = slotMain;
                  mc.playerController.updateController();
               }
            } else {
               mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.breakPos, this.facing));
            }
         }
      }
   }

   @Override
   public void onRender3D(Render3DEvent event) {
      if (!fullNullCheck()) {
         if (this.render.getValue() && this.creativeMode.getValue() && this.cancelStart) {
            if (this.godBlocks.contains(mc.world.getBlockState(this.breakPos).getBlock())) {
               this.empty = true;
            }

            Color color = new Color(this.empty ? 0 : 255, this.empty ? 255 : 0, 0, 255);
            RenderUtil.drawBoxESP(this.breakPos, color, false, color, 1.0F, true, true, 84, false);
         } else if (this.cancelStart && this.render.getValue()) {
            RenderUtil.drawBoxESP(this.breakPos, new Color(255, 255, 255), false, new Color(255, 255, 255), 1.0F, true, true, 84, false);
         }
      }
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck()) {
         if (event.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging packet = event.getPacket();
            if (packet.getAction() == Action.START_DESTROY_BLOCK) {
               event.setCanceled(this.cancelStart);
            }
         }
      }
   }

   @SubscribeEvent
   public void onBlockEvent(PlayerDamageBlockEvent event) {
      if (!fullNullCheck()) {
         if (BlockUtil.canBreak(event.pos)) {
            this.empty = false;
            this.cancelStart = false;
            this.breakPos = event.pos;
            this.breakSuccess.reset();
            this.facing = event.facing;
            if (this.breakPos != null) {
               mc.player.swingArm(EnumHand.MAIN_HAND);
               mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, this.breakPos, this.facing));
               this.cancelStart = true;
               mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.breakPos, this.facing));
               event.setCanceled(true);
            }
         }
      }
   }

   @Override
   public String getDisplayInfo() {
      return this.ghostHand.getValue() ? "Ghost" : "Normal";
   }
}
