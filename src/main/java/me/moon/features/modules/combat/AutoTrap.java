//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.moon.Moon;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.EntityUtil;
import me.moon.util.InventoryUtil;
import me.moon.util.MathUtil;
import me.moon.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoTrap extends Module {
   private final Setting<Integer> delay = this.register(new Setting<>("Delay", 50, 0, 250));
   private final Setting<Integer> blocksPerPlace = this.register(new Setting<>("BlocksPerTick", 8, 1, 30));
   private final Setting<Boolean> togg1e = this.register(new Setting<>("AutoToggle", false));
   private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
   private final Setting<Boolean> smartrotate = this.register(new Setting<>("SmartRotate", true));
   private final Setting<Boolean> packet = this.register(new Setting<>("Packet", true));
   private final Setting<Boolean> raytrace = this.register(new Setting<>("Raytrace", false));
   private final Setting<Boolean> antiScaffold = this.register(new Setting<>("AntiScaffold", false));
   private final Setting<Boolean> antiStep = this.register(new Setting<>("AntiStep", false));
   private final Timer timer = new Timer();
   private final Map<BlockPos, Integer> retries = new HashMap<>();
   private final Timer retryTimer = new Timer();
   public EntityPlayer target;
   private boolean didPlace = false;
   private boolean isSneaking;
   private int lastHotbarSlot;
   private int placements = 0;
   private boolean smartRotate = false;
   private BlockPos startPos = null;

   public AutoTrap() {
      super("AutoTrap", "Traps other players", Module.Category.COMBAT, true, false, false);
   }

   @Override
   public void onEnable() {
      if (!fullNullCheck()) {
         this.startPos = EntityUtil.getRoundedBlockPos(mc.player);
         this.lastHotbarSlot = mc.player.inventory.currentItem;
         this.retries.clear();
      }
   }

   @Override
   public void onTick() {
      if (!fullNullCheck()) {
         this.smartRotate = this.smartrotate.getValue();
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
         this.doStaticTrap();
         if (this.didPlace) {
            this.timer.reset();
         }
      }
   }

   private void doStaticTrap() {
      List<Vec3d> placeTargets = EntityUtil.targets(
         this.target.getPositionVector(), this.antiScaffold.getValue(), this.antiStep.getValue(), false, false, false, this.raytrace.getValue()
      );
      this.placeList(placeTargets);
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
         if (placeability == 1 && (this.retries.get(position) == null || this.retries.get(position) < 4)) {
            this.placeBlock(position);
            this.retries.put(position, this.retries.get(position) == null ? 1 : this.retries.get(position) + 1);
            this.retryTimer.reset();
         } else if (placeability == 3) {
            this.placeBlock(position);
         }
      }
   }

   private boolean check() {
      this.didPlace = false;
      this.placements = 0;
      int obbySlot2 = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      if (obbySlot2 == -1) {
         this.toggle();
      }

      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      if (this.isOff()) {
         return true;
      } else if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.player))) {
         this.disable();
         return true;
      } else {
         if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
         }

         if (obbySlot == -1) {
            Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
            this.disable();
            return true;
         } else {
            if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != obbySlot) {
               this.lastHotbarSlot = mc.player.inventory.currentItem;
            }

            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
            this.target = this.getTarget(10.0, true);
            return this.target == null || !this.timer.passedMs((long)this.delay.getValue().intValue());
         }
      }
   }

   private EntityPlayer getTarget(double range, boolean trapped) {
      EntityPlayer target = null;
      double distance = Math.pow(range, 2.0) + 1.0;

      for(EntityPlayer player : mc.world.playerEntities) {
         if (!EntityUtil.isntValid(player, range)
            && (!trapped || !EntityUtil.isTrapped(player, this.antiScaffold.getValue(), this.antiStep.getValue(), false, false, false))
            && !(Moon.speedManager.getPlayerSpeed(player) > 10.0)) {
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
      if (this.placements < this.blocksPerPlace.getValue() && mc.player.getDistanceSq(pos) <= MathUtil.square(5.0)) {
         int originalSlot = mc.player.inventory.currentItem;
         int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
         int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
         if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
         }

         if (this.smartRotate) {
            mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
            mc.playerController.updateController();
            this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking);
            mc.player.inventory.currentItem = originalSlot;
            mc.playerController.updateController();
         } else {
            mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
            mc.playerController.updateController();
            this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking);
            mc.player.inventory.currentItem = originalSlot;
            mc.playerController.updateController();
         }

         this.didPlace = true;
         ++this.placements;
      }
   }
}
