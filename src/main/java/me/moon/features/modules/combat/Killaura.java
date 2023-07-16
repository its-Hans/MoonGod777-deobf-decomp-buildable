//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.combat;

import me.moon.Moon;
import me.moon.event.events.UpdateWalkingPlayerEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.EntityUtil;
import me.moon.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Killaura extends Module {
   public Entity target;
   private static Killaura INSTANCE = new Killaura();
   private Setting<Float> range = this.register(new Setting<>("Range", 6.0F, 0.1F, 6.0F));
   private Setting<Double> hitDelay = this.register(new Setting<>("HitDelay", 1.0, 0.0, 1.0));
   private Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
   private Setting<Boolean> onlySharp = this.register(new Setting<>("SwordOnly", true));
   public Setting<Boolean> tpsSync = this.register(new Setting<>("TpsSync", true));
   private Setting<Double> raytrace = this.register(new Setting<>("Raytrace", 3.0, 0.0, 3.0));
   private Setting<Boolean> players = this.register(new Setting<>("Players", true));
   private Setting<Boolean> mobs = this.register(new Setting<>("Mobs", false));
   private Setting<Boolean> animals = this.register(new Setting<>("Animals", false));
   private Setting<Boolean> vehicles = this.register(new Setting<>("Entities", false));
   private Setting<Boolean> projectiles = this.register(new Setting<>("Projectiles", false));

   public Killaura() {
      super("Killaura", "Kills aura.", Module.Category.COMBAT, true, false, false);
      this.setInstance();
   }

   public static Killaura getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Killaura();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @Override
   public void onTick() {
      if (!this.rotate.getValue()) {
         this.doKillaura();
      }
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0 && this.rotate.getValue()) {
         this.doKillaura();
      }
   }

   private void doKillaura() {
      this.target = this.getTarget();
      if (!this.onlySharp.getValue() || EntityUtil.holdingWeapon(mc.player)) {
         if (this.target != null) {
            if (this.rotate.getValue()) {
               Moon.rotationManager.lookAtEntity(this.target);
            }

            if (Auto32k.getInstance().isOn() && Auto32k.getInstance().smartHit$XIN.getValue() && this.target instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)this.target;
               if ((double)(player.getHealth() + player.getAbsorptionAmount()) >= 28.0 && (double)mc.player.getCooledAttackStrength(0.0F) < 0.3
                  || (double)(player.getHealth() + player.getAbsorptionAmount()) <= 28.0
                     && (double)mc.player.getCooledAttackStrength(0.0F) < this.hitDelay.getValue()) {
                  return;
               }
            } else if ((double)mc.player.getCooledAttackStrength(0.0F) < this.hitDelay.getValue()) {
               return;
            }

            EntityUtil.attackEntity(this.target, false, EnumHand.MAIN_HAND);
         }
      }
   }

   private Entity getTarget() {
      Entity closestEntity = null;

      for(Entity entity : mc.world.loadedEntityList) {
         if ((
               this.players.getValue() && entity instanceof EntityPlayer
                  || this.mobs.getValue() && EntityUtil.isMobAggressive(entity)
                  || this.animals.getValue() && EntityUtil.isPassive(entity)
                  || this.vehicles.getValue() && EntityUtil.isVehicle(entity)
                  || this.projectiles.getValue() && EntityUtil.isProjectile(entity)
            )
            && (!(entity instanceof EntityLivingBase) || !EntityUtil.isntValid(entity, (double)this.range.getValue().floatValue()))
            && (
               mc.player.canEntityBeSeen(entity)
                  || !(mc.player.getDistanceSq(entity) > MathUtil.square((double)this.raytrace.getValue().floatValue()))
            )) {
            if (closestEntity == null) {
               closestEntity = entity;
            } else if (closestEntity.getDistance(mc.player) > entity.getDistance(mc.player)) {
               closestEntity = entity;
            }
         }
      }

      return closestEntity;
   }

   @Override
   public String getDisplayInfo() {
      return this.target instanceof EntityPlayer ? this.target.getName() : null;
   }
}
