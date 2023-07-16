//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import me.moon.features.modules.Module;
import me.moon.features.modules.player.MoonBreak;
import me.moon.features.setting.Bind;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.EntityUtil;
import me.moon.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FeetBreak extends Module {
   public EntityPlayer target;
   private Setting<Bind> bind = this.register(new Setting<>("Enable", new Bind(-1)));

   public FeetBreak() {
      super("FeetBreak", "FeetBreak", Module.Category.COMBAT, true, false, false);
   }

   @Override
   public void onTick() {
      if (!fullNullCheck()) {
         if (InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1) {
            this.target = this.getTarget(5.0);
            this.surroundMine();
         }
      }
   }

   @Override
   public String getDisplayInfo() {
      return this.target != null ? this.target.getName() : null;
   }

   private void surroundMine() {
      if (this.target != null && this.bind.getValue().isDown()) {
         Vec3d a = this.target.getPositionVector();
         if (EntityUtil.getSurroundWeakness(a, 1, -1)) {
            this.surroundMine(a, -1.0, 0.0, 0.0);
         } else if (EntityUtil.getSurroundWeakness(a, 2, -1)) {
            this.surroundMine(a, 1.0, 0.0, 0.0);
         } else if (EntityUtil.getSurroundWeakness(a, 3, -1)) {
            this.surroundMine(a, 0.0, 0.0, -1.0);
         } else if (EntityUtil.getSurroundWeakness(a, 4, -1)) {
            this.surroundMine(a, 0.0, 0.0, 1.0);
         } else if (EntityUtil.getSurroundWeakness(a, 5, -1)) {
            this.surroundMine(a, -1.0, 0.0, 0.0);
         } else if (EntityUtil.getSurroundWeakness(a, 6, -1)) {
            this.surroundMine(a, 1.0, 0.0, 0.0);
         } else if (EntityUtil.getSurroundWeakness(a, 7, -1)) {
            this.surroundMine(a, 0.0, 0.0, -1.0);
         } else if (EntityUtil.getSurroundWeakness(a, 8, -1)) {
            this.surroundMine(a, 0.0, 0.0, 1.0);
         }
      }
   }

   private void surroundMine(Vec3d pos, double x, double y, double z) {
      BlockPos position = new BlockPos(pos).add(x, y, z);
      if (MoonBreak.getInstance().isOff()) {
         MoonBreak.getInstance().enable();
      } else if (MoonBreak.getInstance().isOn()) {
         mc.playerController.onPlayerDamageBlock(position, BlockUtil.getRayTraceFacing(position));
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
