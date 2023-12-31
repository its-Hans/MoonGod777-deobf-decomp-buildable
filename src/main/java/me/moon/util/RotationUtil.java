//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil implements Util {
   public static Vec3d getEyesPos() {
      return new Vec3d(
         mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ
      );
   }

   public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
      double dirx = me.posX - px;
      double diry = me.posY - py;
      double dirz = me.posZ - pz;
      double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
      double var20;
      double pitch = Math.asin(var20 = diry / len);
      double var19;
      double var21;
      double yaw = Math.atan2(var21 = dirz / len, var19 = dirx / len);
      pitch = pitch * 180.0 / Math.PI;
      yaw = yaw * 180.0 / Math.PI;
      double var24;
      return new double[]{var24 = yaw + 90.0, pitch};
   }

   public static float[] getLegitRotations(Vec3d vec) {
      Vec3d eyesPos = getEyesPos();
      double diffX = vec.x - eyesPos.x;
      double diffY = vec.y - eyesPos.y;
      double diffZ = vec.z - eyesPos.z;
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
      return new float[]{
         mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
         mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
      };
   }

   public static void faceYawAndPitch(float yaw, float pitch) {
      mc.player.connection.sendPacket(new Rotation(yaw, pitch, mc.player.onGround));
   }

   public static void faceVector(Vec3d vec, boolean normalizeAngle) {
      float[] rotations = getLegitRotations(vec);
      mc.player
         .connection
         .sendPacket(
            new Rotation(rotations[0], normalizeAngle ? (float)MathHelper.normalizeAngle((int)rotations[1], 360) : rotations[1], mc.player.onGround)
         );
   }

   public static void faceEntity(Entity entity) {
      float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
      faceYawAndPitch(angle[0], angle[1]);
   }

   public static float[] getAngle(Entity entity) {
      return MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
   }

   public static int getDirection4D() {
      return MathHelper.floor((double)(mc.player.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;
   }

   public static String getDirection4D(boolean northRed) {
      int dirnumber = getDirection4D();
      if (dirnumber == 0) {
         return "South (+Z)";
      } else if (dirnumber == 1) {
         return "West (-X)";
      } else if (dirnumber == 2) {
         return (northRed ? "Â§c" : "") + "North (-Z)";
      } else {
         return dirnumber == 3 ? "East (+X)" : "Loading...";
      }
   }
}
