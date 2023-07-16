//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.moon.Moon;
import me.moon.event.events.PacketEvent;
import me.moon.event.events.Render3DEvent;
import me.moon.features.modules.Module;
import me.moon.features.modules.client.ClickGui;
import me.moon.features.modules.misc.AutoGG;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import me.moon.util.ColorUtil;
import me.moon.util.DamageUtil;
import me.moon.util.EntityUtil;
import me.moon.util.MathUtil;
import me.moon.util.RenderUtil;
import me.moon.util.Timer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MoonAura extends Module {
   private final Timer placeTimer = new Timer();
   private final Timer breakTimer = new Timer();
   private final Setting<MoonAura.Settings> setting = this.register(new Setting<>("Settings", MoonAura.Settings.PLACE));
   private Setting<Boolean> place = this.register(new Setting<>("Place", true, v -> this.setting.getValue() == MoonAura.Settings.PLACE));
   private Setting<Double> placeDelay = this.register(
      new Setting<>("PlaceDelay", 0.0, 0.0, 400.0, v -> this.place.getValue() && this.setting.getValue() == MoonAura.Settings.PLACE)
   );
   private Setting<Double> placeRange = this.register(
      new Setting<>("PlaceRange", 5.0, 0.0, 6.0, v -> this.place.getValue() && this.setting.getValue() == MoonAura.Settings.PLACE)
   );
   private Setting<Boolean> explode = this.register(new Setting<>("Break", true, v -> this.setting.getValue() == MoonAura.Settings.BREAK));
   private Setting<Boolean> packetBreak = this.register(
      new Setting<>("PacketBreak", true, v -> this.explode.getValue() && this.setting.getValue() == MoonAura.Settings.BREAK)
   );
   private Setting<Boolean> predictsBreak = this.register(new Setting<>("PredictsBreak", true, v -> this.setting.getValue() == MoonAura.Settings.BREAK));
   private Setting<Boolean> fastUpdate = this.register(new Setting<>("FastUpDate", true, v -> this.setting.getValue() == MoonAura.Settings.BREAK));
   private Setting<Double> breakDelay = this.register(
      new Setting<>("BreakDelay", 0.0, 0.0, 400.0, v -> this.explode.getValue() && this.setting.getValue() == MoonAura.Settings.BREAK)
   );
   private Setting<Double> breakRange = this.register(
      new Setting<>("BreakRange", 6.0, 0.0, 6.0, v -> this.explode.getValue() && this.setting.getValue() == MoonAura.Settings.BREAK)
   );
   private Setting<Boolean> rotate = this.register(new Setting<>("Rotate", false, v -> this.setting.getValue() == MoonAura.Settings.MISC));
   private Setting<Boolean> antiSuicide = this.register(new Setting<>("AntiSuicide", true, v -> this.setting.getValue() == MoonAura.Settings.MISC));
   private Setting<Double> raytrace = this.register(new Setting<>("Raytrace", 3.0, 0.0, 3.0, v -> this.setting.getValue() == MoonAura.Settings.MISC));
   private Setting<Double> targetRange = this.register(new Setting<>("TargetRange", 12.0, 0.0, 12.0, v -> this.setting.getValue() == MoonAura.Settings.MISC));
   private Setting<Double> minPlaceDmg = this.register(new Setting<>("MinPlaceDmg", 6.0, 0.0, 24.0, v -> this.setting.getValue() == MoonAura.Settings.MISC));
   private Setting<Double> minBreakDmg = this.register(new Setting<>("MinBreakDmg", 6.0, 0.0, 24.0, v -> this.setting.getValue() == MoonAura.Settings.MISC));
   private Setting<Double> maxSelfDmg = this.register(new Setting<>("MaxSelfDmg", 9.5, 0.0, 12.0, v -> this.setting.getValue() == MoonAura.Settings.MISC));
   private Setting<MoonAura.SwingMode> breakSwing = this.register(
      new Setting<>("BreakSwing", MoonAura.SwingMode.MainHand, v -> this.setting.getValue() == MoonAura.Settings.MISC)
   );
   private Setting<Boolean> render = this.register(new Setting<>("Render", true, v -> this.setting.getValue() == MoonAura.Settings.RENDER));
   private Setting<Boolean> renderDmg = this.register(
      new Setting<>("RenderDmg", true, v -> this.render.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Boolean> box = this.register(new Setting<>("Box", true, v -> this.render.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER));
   private Setting<Boolean> outline = this.register(
      new Setting<>("Outline", true, v -> this.render.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Integer> red = this.register(
      new Setting<>("Red", 255, 0, 255, v -> this.render.getValue() && this.box.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Integer> green = this.register(
      new Setting<>("Green", 255, 0, 255, v -> this.render.getValue() && this.box.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Integer> blue = this.register(
      new Setting<>("Blue", 255, 0, 255, v -> this.render.getValue() && this.box.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Integer> alpha = this.register(
      new Setting<>("Alpha", 200, 0, 255, v -> this.render.getValue() && this.box.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Integer> boxAlpha = this.register(
      new Setting<>("BoxAlpha", 100, 0, 255, v -> this.render.getValue() && this.box.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Float> lineWidth = this.register(
      new Setting<>("LineWidth", 1.0F, 0.1F, 5.0F, v -> this.render.getValue() && this.box.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Integer> cRed = this.register(
      new Setting<>("OL-Red", 255, 0, 255, v -> this.render.getValue() && this.outline.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Integer> cGreen = this.register(
      new Setting<>("OL-Green", 255, 0, 255, v -> this.render.getValue() && this.outline.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Integer> cBlue = this.register(
      new Setting<>("OL-Blue", 255, 0, 255, v -> this.render.getValue() && this.outline.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private Setting<Integer> cAlpha = this.register(
      new Setting<>("OL-Alpha", 200, 0, 255, v -> this.render.getValue() && this.outline.getValue() && this.setting.getValue() == MoonAura.Settings.RENDER)
   );
   private EntityEnderCrystal crystal;
   private boolean rotating = false;
   private String displaytarget;
   private EntityPlayer target;
   private float pitch = 0.0F;
   private float yaw = 0.0F;
   private BlockPos pos;

   public MoonAura() {
      super("AutoCrystal", "Version: 0.5.3", Module.Category.COMBAT, true, false, false);
   }

   @Override
   public void onEnable() {
      this.placeTimer.reset();
      this.breakTimer.reset();
      this.target = null;
      this.displaytarget = null;
      this.crystal = null;
      this.pos = null;
   }

   @Override
   public void onDisable() {
      this.rotating = false;
   }

   @Override
   public void onTick() {
      if (!fullNullCheck()) {
         if (mc.currentScreen instanceof GuiChest
            && mc.world.getBlockState(mc.player.rayTrace(4.5, mc.getRenderPartialTicks()).getBlockPos()).getBlock()
               == Blocks.ENDER_CHEST) {
            mc.displayGuiScreen(null);
         }

         if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL
            && mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
            this.target = null;
            this.pos = null;
         } else {
            if (this.target == null) {
               this.target = this.getTarget();
            }

            if (this.target == null) {
               this.crystal = null;
            } else {
               this.crystal = (EntityEnderCrystal)mc.world
                  .loadedEntityList
                  .stream()
                  .filter(this::$Hit1)
                  .map(crystal -> (EntityEnderCrystal)crystal)
                  .min(Comparator.comparing(crystal -> this.target.getDistance(crystal)))
                  .orElse(null);
               if (this.crystal != null && this.explode.getValue() && this.breakTimer.passedMs(this.breakDelay.getValue().longValue())) {
                  this.breakTimer.reset();
                  if (this.packetBreak.getValue()) {
                     this.rotateTo(this.crystal);
                     EntityUtil.attackEntity(
                        this.crystal, true, this.breakSwing.getValue() == MoonAura.SwingMode.MainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND
                     );
                  } else {
                     this.rotateTo(this.crystal);
                     EntityUtil.attackEntity(
                        this.crystal, false, this.breakSwing.getValue() == MoonAura.SwingMode.MainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND
                     );
                     mc.player.resetCooldown();
                  }
               }

               if (this.place.getValue() && this.placeTimer.passedMs(this.placeDelay.getValue().longValue())) {
                  this.placeTimer.reset();
                  double damage = 1.5;

                  for(BlockPos blockPos : this.possiblePlacePositions(this.placeRange.getValue().floatValue())) {
                     if (this.target != null
                        && blockPos != null
                        && !this.target.isDead
                        && !(this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0F)) {
                        double targetDamage = (double)DamageUtil.calculateDamage(
                           (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5, this.target
                        );
                        if (!(targetDamage < this.minPlaceDmg.getValue()) && !(damage >= targetDamage)) {
                           this.pos = blockPos;
                           damage = targetDamage;
                        }
                     }
                  }

                  if (damage == 1.5) {
                     this.target = null;
                     this.pos = null;
                     return;
                  }

                  if (this.pos != null) {
                     this.rotateToPos(this.pos);
                     BlockUtil.placeCrystalOnBlock(
                        this.pos, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND
                     );
                  }
               }
            }
         }
      }
   }

   private void rotateTo(Entity entity) {
      if (this.rotate.getValue()) {
         float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
         this.yaw = angle[0];
         this.pitch = angle[1];
         this.rotating = true;
      }
   }

   private void rotateToPos(BlockPos pos) {
      if (this.rotate.getValue()) {
         float[] angle = MathUtil.calcAngle(
            mc.player.getPositionEyes(mc.getRenderPartialTicks()),
            new Vec3d((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() - 0.5F), (double)((float)pos.getZ() + 0.5F))
         );
         this.yaw = angle[0];
         this.pitch = angle[1];
         this.rotating = true;
      }
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (this.rotate.getValue() && this.rotating && event.getPacket() instanceof CPacketPlayer) {
         CPacketPlayer packet = event.getPacket();
         packet.yaw = this.yaw;
         packet.pitch = this.pitch;
         this.rotating = false;
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST,
      receiveCanceled = true
   )
   public void onPacketReceive(PacketEvent.Receive event) {
      try {
         if (this.predictsBreak.getValue() && event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51 && this.$Hit2(new BlockPos(packet.getX(), packet.getY(), packet.getZ()))) {
               CPacketUseEntity predict = new CPacketUseEntity();
               predict.entityId = packet.getEntityID();
               predict.action = Action.ATTACK;
               mc.player.connection.sendPacket(predict);
            }
         }

         if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = event.getPacket();
            if (this.fastUpdate.getValue()
               && this.crystal != null
               && packet.getCategory() == SoundCategory.BLOCKS
               && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE
               && this.$Hit2(new BlockPos(packet.getX(), packet.getY(), packet.getZ()))) {
               this.crystal.setDead();
            }
         }
      } catch (NullPointerException var4) {
      }
   }

   @Override
   public void onRender3D(Render3DEvent event) {
      if (this.pos != null && this.target != null) {
         RenderUtil.drawBoxESP(
            this.pos,
            ClickGui.getInstance().rainbow.getValue()
               ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue())
               : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()),
            this.outline.getValue(),
            ClickGui.getInstance().rainbow.getValue()
               ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue())
               : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()),
            this.lineWidth.getValue(),
            this.outline.getValue(),
            this.box.getValue(),
            this.boxAlpha.getValue(),
            true
         );
         double renderDamage = (double)DamageUtil.calculateDamage(
            (double)this.pos.getX() + 0.5, (double)this.pos.getY() + 1.0, (double)this.pos.getZ() + 0.5, this.target
         );
         int color = renderDamage > 18.0
            ? 16711680
            : (
               renderDamage > 16.0
                  ? 16720896
                  : (renderDamage > 12.0 ? 16744192 : (renderDamage > 8.0 ? 16776960 : (renderDamage > 5.0 ? '\uffff' : '\uff00')))
            );
         if (this.renderDmg.getValue()) {
            RenderUtil.drawText(this.pos, (Math.floor(renderDamage) == renderDamage ? (int)renderDamage : String.format("%.1f", renderDamage)) + "", color);
         }
      }
   }

   @Override
   public String getDisplayInfo() {
      if (this.target != null) {
         this.displaytarget = this.target.getName();
      }

      if (this.displaytarget != null) {
         if (AutoGG.getInstance().isOn()) {
            AutoGG duel = (AutoGG)Moon.moduleManager.getModuleByName("AutoGG");
            duel.target = this.displaytarget;
         }

         return this.displaytarget;
      } else {
         return null;
      }
   }

   private EntityPlayer getTarget() {
      EntityPlayer closestPlayer = null;

      for(EntityPlayer entity : mc.world.playerEntities) {
         if (mc.player != null
            && !mc.player.isDead
            && !entity.isDead
            && !(entity.getHealth() + entity.getAbsorptionAmount() <= 0.0F)
            && !entity.getName().equals(mc.player.getName())
            && !Moon.friendManager.isFriend(entity.getName())
            && !entity.isCreative()
            && !EntityUtil.isInHole(entity)
            && !((double)entity.getDistance(mc.player) > this.targetRange.getValue())) {
            if (closestPlayer == null) {
               closestPlayer = entity;
            } else if (closestPlayer.getDistance(mc.player) > entity.getDistance(mc.player)) {
               closestPlayer = entity;
            }
         }
      }

      return closestPlayer;
   }

   private boolean $Hit1(Entity p_Entity) {
      if (p_Entity == null) {
         return false;
      } else if (!(p_Entity instanceof EntityEnderCrystal)) {
         return false;
      } else if (p_Entity.isDead) {
         return false;
      } else if (this.target == null) {
         return false;
      } else if (Math.sqrt(mc.player.getDistanceSq(p_Entity.posX, p_Entity.posY, p_Entity.posZ)) > this.breakRange.getValue()
         )
       {
         return false;
      } else if (!mc.player.canEntityBeSeen(p_Entity)
         && Math.sqrt(mc.player.getDistanceSq(p_Entity.posX, p_Entity.posY, p_Entity.posZ)) > this.raytrace.getValue()) {
         return false;
      } else if (!this.target.isDead && !(this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0F)) {
         double selfDamage = (double)DamageUtil.calculateDamage(p_Entity.posX, p_Entity.posY, p_Entity.posZ, mc.player);
         double targetDamage = (double)DamageUtil.calculateDamage(p_Entity.posX, p_Entity.posY, p_Entity.posZ, this.target);
         if (!(selfDamage + (this.antiSuicide.getValue() ? 2.0 : 0.5) >= (double)(mc.player.getHealth() + mc.player.getAbsorptionAmount()))
            && !(selfDamage - 0.5 > this.maxSelfDmg.getValue())) {
            return targetDamage >= (double)(this.target.getHealth() + this.target.getAbsorptionAmount()) || targetDamage >= this.minBreakDmg.getValue();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean $Hit2(BlockPos packet) {
      if (packet == null) {
         return false;
      } else if (this.target == null) {
         return false;
      } else if (Math.sqrt(mc.player.getDistanceSq((double)packet.getX(), (double)packet.getY(), (double)packet.getZ()))
         > this.breakRange.getValue()) {
         return false;
      } else if (!BlockUtil.canBlockBeSeen((double)packet.getX(), (double)packet.getY(), (double)packet.getZ())
         && Math.sqrt(mc.player.getDistanceSq((double)packet.getX(), (double)packet.getY(), (double)packet.getZ()))
            > this.raytrace.getValue()) {
         return false;
      } else if (!this.target.isDead && !(this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0F)) {
         double selfDamage = (double)DamageUtil.calculateDamage(
            (double)packet.getX(), (double)packet.getY(), (double)packet.getZ(), mc.player
         );
         double targetDamage = (double)DamageUtil.calculateDamage(
            (double)packet.getX(), (double)packet.getY(), (double)packet.getZ(), this.target
         );
         if (!(selfDamage + (this.antiSuicide.getValue() ? 2.0 : 0.5) >= (double)(mc.player.getHealth() + mc.player.getAbsorptionAmount()))
            && !(selfDamage - 0.5 > this.maxSelfDmg.getValue())) {
            return targetDamage >= (double)(this.target.getHealth() + this.target.getAbsorptionAmount()) || targetDamage >= this.minBreakDmg.getValue();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private List<BlockPos> possiblePlacePositions(float placeRange) {
      NonNullList<BlockPos> positions = NonNullList.create();
      positions.addAll(
         BlockUtil.getSphere(
               new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)),
               placeRange,
               (int)placeRange,
               false,
               true,
               0
            )
            .stream()
            .filter(pos -> BlockUtil.canPlaceCrystal(pos))
            .collect(Collectors.toList())
      );
      positions.removeIf(
         blockPos -> {
            if (!BlockUtil.canBlockBeSeen(
                  (double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5
               )
               && Math.sqrt(
                     mc.player
                        .getDistanceSq((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5)
                  )
                  > this.raytrace.getValue()) {
               return true;
            } else {
               double selfDamage = (double)DamageUtil.calculateDamage(
                  (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5, mc.player
               );
               return selfDamage + (this.antiSuicide.getValue() ? 2.0 : 0.5)
                     >= (double)(mc.player.getHealth() + mc.player.getAbsorptionAmount())
                  || selfDamage - 0.5 > this.maxSelfDmg.getValue();
            }
         }
      );
      return positions;
   }

   public static enum Settings {
      PLACE,
      BREAK,
      MISC,
      RENDER;
   }

   public static enum SwingMode {
      MainHand,
      OffHand;
   }
}
