//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.render;

import java.awt.Color;
import me.moon.event.events.Render3DEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.EntityUtil;
import me.moon.util.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SurroundRender extends Module {
   public EntityPlayer target;
   private final Setting<Integer> range = this.register(new Setting<>("Range", 5, 1, 10));

   public SurroundRender() {
      super("SurroundRender", "SurroundRender", Module.Category.RENDER, true, false, false);
   }

   @Override
   public void onRender3D(Render3DEvent event) {
      if (!fullNullCheck()) {
         this.target = this.getTarget((double)this.range.getValue().intValue());
         this.surroundRender();
      }
   }

   private void surroundRender() {
      if (this.target != null) {
         Vec3d a = this.target.getPositionVector();
         if (mc.world.getBlockState(new BlockPos(a)).getBlock() == Blocks.OBSIDIAN
            || mc.world.getBlockState(new BlockPos(a)).getBlock() == Blocks.ENDER_CHEST) {
            RenderUtil.drawBoxESP(new BlockPos(a), new Color(255, 255, 0), false, new Color(255, 255, 0), 1.0F, false, true, 42, true);
         }

         if (EntityUtil.getSurroundWeakness(a, -1, 1)) {
            this.surroundRender(a, -1.0, 0.0, 0.0, true);
         }

         if (EntityUtil.getSurroundWeakness(a, -1, 2)) {
            this.surroundRender(a, 1.0, 0.0, 0.0, true);
         }

         if (EntityUtil.getSurroundWeakness(a, -1, 3)) {
            this.surroundRender(a, 0.0, 0.0, -1.0, true);
         }

         if (EntityUtil.getSurroundWeakness(a, -1, 4)) {
            this.surroundRender(a, 0.0, 0.0, 1.0, true);
         }

         if (EntityUtil.getSurroundWeakness(a, -1, 5)) {
            this.surroundRender(a, -1.0, 0.0, 0.0, false);
         }

         if (EntityUtil.getSurroundWeakness(a, -1, 6)) {
            this.surroundRender(a, 1.0, 0.0, 0.0, false);
         }

         if (EntityUtil.getSurroundWeakness(a, -1, 7)) {
            this.surroundRender(a, 0.0, 0.0, -1.0, false);
         }

         if (EntityUtil.getSurroundWeakness(a, -1, 8)) {
            this.surroundRender(a, 0.0, 0.0, 1.0, false);
         }
      }
   }

   private void surroundRender(Vec3d pos, double x, double y, double z, boolean red) {
      BlockPos position = new BlockPos(pos).add(x, y, z);
      if (mc.world.getBlockState(position).getBlock() != Blocks.AIR
         && mc.world.getBlockState(position).getBlock() != Blocks.FIRE) {
         if (red) {
            RenderUtil.drawBoxESP(position, new Color(255, 0, 0), false, new Color(255, 0, 0), 1.0F, false, true, 42, true);
         } else {
            RenderUtil.drawBoxESP(position, new Color(0, 0, 255), false, new Color(0, 0, 255), 1.0F, false, true, 42, true);
         }
      }
   }

   private EntityPlayer getTarget(double range) {
      EntityPlayer target = null;
      double distance = range;

      for(EntityPlayer player : mc.world.playerEntities) {
         if (!EntityUtil.isntValid(player, range) && EntityUtil.isInHole(player)) {
            if (target == null) {
               target = player;
               distance = mc.player.getDistanceSq(player);
            } else if (mc.player.getDistanceSq(player) < distance) {
               target = player;
               distance = mc.player.getDistanceSq(player);
            }
         }
      }

      return target;
   }
}
