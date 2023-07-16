//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.moon.Moon;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.EntityUtil;
import me.moon.util.InventoryUtil;
import me.moon.util.MathUtil;
import me.moon.util.Timer;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoWeb extends Module {
   private final Setting<Integer> delay = this.register(new Setting<>("Delay", 50, 0, 250));
   private final Setting<Integer> blocksPerPlace = this.register(new Setting<>("BlocksPerTick", 8, 1, 30));
   private final Setting<Boolean> packet = this.register(new Setting<>("PacketPlace", false));
   private final Setting<Boolean> disable = this.register(new Setting<>("AutoDisable", false));
   private final Setting<Boolean> togg1e = this.register(new Setting<>("AutoToggle", false));
   private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
   private final Setting<Boolean> raytrace = this.register(new Setting<>("Raytrace", false));
   private final Setting<Boolean> lowerbody = this.register(new Setting<>("Feet", true));
   private final Setting<Boolean> upperBody = this.register(new Setting<>("Face", false));
   private final Timer timer = new Timer();
   public EntityPlayer target;
   private boolean didPlace = false;
   private boolean isSneaking;
   private int placements = 0;
   private boolean smartRotate = false;
   private BlockPos startPos = null;

   public AutoWeb() {
      super("AutoWeb", "Traps other players in webs", Module.Category.COMBAT, true, false, false);
   }

   @Override
   public void onEnable() {
      if (!fullNullCheck()) {
         this.startPos = EntityUtil.getRoundedBlockPos(mc.player);
      }
   }

   @Override
   public void onTick() {
      if (InventoryUtil.findHotbarBlock(BlockWeb.class) == -1) {
         Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "Web ?");
         this.disable();
      } else {
         this.smartRotate = false;
         this.doTrap();
         if (this.togg1e.getValue()) {
            this.toggle();
         }
      }
   }

   @Override
   public String getDisplayInfo() {
      return this.target != null ? this.target.getName() : null;
   }

   @Override
   public void onDisable() {
      this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
   }

   private void doTrap() {
      if (!this.check()) {
         this.doWebTrap();
         if (this.didPlace) {
            this.timer.reset();
         }
      }
   }

   private void doWebTrap() {
      List<Vec3d> placeTargets = this.getPlacements();
      int a = mc.player.inventory.currentItem;
      mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(BlockWeb.class);
      mc.playerController.updateController();
      this.placeList(placeTargets);
      mc.player.inventory.currentItem = a;
      mc.playerController.updateController();
   }

   private List<Vec3d> getPlacements() {
      ArrayList<Vec3d> list = new ArrayList();
      Vec3d baseVec = this.target.getPositionVector();
      if (this.lowerbody.getValue()) {
         list.add(baseVec);
      }

      if (this.upperBody.getValue()) {
         list.add(baseVec.add(0.0, 1.0, 0.0));
      }

      return list;
   }

   private void placeList(List<Vec3d> list) {
      list.sort(
         (vec3d, vec3d2) -> Double.compare(
               mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z),
               mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)
            )
      );
      list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));

      for(Vec3d vec3d3 : list) {
         BlockPos position = new BlockPos(vec3d3);
         int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValue());
         if (placeability == 3 || placeability == 1) {
            this.placeBlock(position);
         }
      }
   }

   private boolean check() {
      this.didPlace = false;
      this.placements = 0;
      if (this.isOff()) {
         return true;
      } else if (this.disable.getValue() && !this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.player))) {
         this.disable();
         return true;
      } else {
         this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
         this.target = this.getTarget(10.0);
         return this.target == null || !this.timer.passedMs((long)this.delay.getValue().intValue());
      }
   }

   private EntityPlayer getTarget(double range) {
      EntityPlayer target = null;
      double distance = Math.pow(range, 2.0) + 1.0;

      for(EntityPlayer player : mc.world.playerEntities) {
         if (!EntityUtil.isntValid(player, range) && !player.isInWeb && !(Moon.speedManager.getPlayerSpeed(player) > 30.0)) {
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

   private void placeBlock(BlockPos pos) {
      if (this.placements < this.blocksPerPlace.getValue() && mc.player.getDistanceSq(pos) <= MathUtil.square(6.0)) {
         this.isSneaking = this.smartRotate
            ? BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking)
            : BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking);
         this.didPlace = true;
         ++this.placements;
      }
   }
}
