//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.manager;

import me.moon.features.Feature;
import me.moon.util.MathUtil;
import me.moon.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RotationManager extends Feature {
   private float yaw;
   private float pitch;

   public void updateRotations() {
      this.yaw = mc.player.rotationYaw;
      this.pitch = mc.player.rotationPitch;
   }

   public void restoreRotations() {
      mc.player.rotationYaw = this.yaw;
      mc.player.rotationYawHead = this.yaw;
      mc.player.rotationPitch = this.pitch;
   }

   public void setPlayerRotations(float yaw, float pitch) {
      mc.player.rotationYaw = yaw;
      mc.player.rotationYawHead = yaw;
      mc.player.rotationPitch = pitch;
   }

   public void setPlayerYaw(float yaw) {
      mc.player.rotationYaw = yaw;
      mc.player.rotationYawHead = yaw;
   }

   public void lookAtPos(BlockPos pos) {
      float[] angle = MathUtil.calcAngle(
         mc.player.getPositionEyes(mc.getRenderPartialTicks()),
         new Vec3d((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F))
      );
      this.setPlayerRotations(angle[0], angle[1]);
   }

   public void lookAtVec3d(Vec3d vec3d) {
      float[] angle = MathUtil.calcAngle(
         mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z)
      );
      this.setPlayerRotations(angle[0], angle[1]);
   }

   public void lookAtVec3d(double x, double y, double z) {
      Vec3d vec3d = new Vec3d(x, y, z);
      this.lookAtVec3d(vec3d);
   }

   public void lookAtEntity(Entity entity) {
      float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
      this.setPlayerRotations(angle[0], angle[1]);
   }

   public void setPlayerPitch(float pitch) {
      mc.player.rotationPitch = pitch;
   }

   public float getYaw() {
      return this.yaw;
   }

   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float pitch) {
      this.pitch = pitch;
   }

   public int getDirection4D() {
      return RotationUtil.getDirection4D();
   }

   public String getDirection4D(boolean northRed) {
      return RotationUtil.getDirection4D(northRed);
   }
}
