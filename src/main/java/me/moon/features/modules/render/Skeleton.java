//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.render;

import java.util.HashMap;
import me.moon.Moon;
import me.moon.event.events.Render3DEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.BlockUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Skeleton extends Module {
   private static final HashMap<EntityPlayer, float[][]> entities = new HashMap<>();
   private final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0F, 0.1F, 5.0F));
   private final Setting<Integer> alpha = this.register(new Setting<>("Alpha", 255, 0, 255));
   private final Setting<Boolean> invisibles = this.register(new Setting<>("Invisibles", false));

   public Skeleton() {
      super("Skeleton", "Draws a skeleton inside the player.", Module.Category.RENDER, false, false, false);
   }

   public static void addEntity(EntityPlayer e, ModelPlayer model) {
      entities.put(
         e,
         new float[][]{
            {model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ},
            {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ},
            {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ},
            {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ},
            {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}
         }
      );
   }

   private Vec3d getVec3(Render3DEvent event, EntityPlayer e) {
      float pt = event.getPartialTicks();
      double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)pt;
      double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)pt;
      double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)pt;
      return new Vec3d(x, y, z);
   }

   @Override
   public void onRender3D(Render3DEvent event) {
      if (!fullNullCheck()) {
         this.startEnd(true);
         GL11.glEnable(2903);
         GL11.glDisable(2848);
         entities.keySet().removeIf(this::doesntContain);

         for(EntityPlayer e : mc.world.playerEntities) {
            this.drawSkeleton(event, e);
         }

         Gui.drawRect(0, 0, 0, 0, 0);
         this.startEnd(false);
      }
   }

   private void drawSkeleton(Render3DEvent event, EntityPlayer e) {
      if (BlockUtil.isPosInFov(new BlockPos(e.posX, e.posY, e.posZ))) {
         if (!e.isInvisible() || this.invisibles.getValue()) {
            float[][] entPos = (float[][])entities.get(e);
            if (entPos != null && e.isEntityAlive() && !e.isDead && e != mc.player && !e.isPlayerSleeping()) {
               GL11.glPushMatrix();
               GL11.glEnable(2848);
               GL11.glLineWidth(this.lineWidth.getValue());
               if (Moon.friendManager.isFriend(e.getName())) {
                  GlStateManager.color(0.0F, 191.0F, 230.0F, (float)this.alpha.getValue().intValue());
               } else {
                  GlStateManager.color(255.0F, 0.0F, 0.0F, (float)this.alpha.getValue().intValue());
               }

               Vec3d vec = this.getVec3(event, e);
               double x = vec.x - mc.getRenderManager().renderPosX;
               double y = vec.y - mc.getRenderManager().renderPosY;
               double z = vec.z - mc.getRenderManager().renderPosZ;
               GL11.glTranslated(x, y, z);
               float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
               GL11.glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
               GL11.glTranslated(0.0, 0.0, e.isSneaking() ? -0.235 : 0.0);
               float yOff = e.isSneaking() ? 0.6F : 0.75F;
               GL11.glPushMatrix();
               GL11.glTranslated(-0.125, (double)yOff, 0.0);
               if (entPos[3][0] != 0.0F) {
                  GL11.glRotatef(entPos[3][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }

               if (entPos[3][1] != 0.0F) {
                  GL11.glRotatef(entPos[3][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (entPos[3][2] != 0.0F) {
                  GL11.glRotatef(entPos[3][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               GL11.glBegin(3);
               GL11.glVertex3d(0.0, 0.0, 0.0);
               GL11.glVertex3d(0.0, (double)(-yOff), 0.0);
               GL11.glEnd();
               GL11.glPopMatrix();
               GL11.glPushMatrix();
               GL11.glTranslated(0.125, (double)yOff, 0.0);
               if (entPos[4][0] != 0.0F) {
                  GL11.glRotatef(entPos[4][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }

               if (entPos[4][1] != 0.0F) {
                  GL11.glRotatef(entPos[4][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (entPos[4][2] != 0.0F) {
                  GL11.glRotatef(entPos[4][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               GL11.glBegin(3);
               GL11.glVertex3d(0.0, 0.0, 0.0);
               GL11.glVertex3d(0.0, (double)(-yOff), 0.0);
               GL11.glEnd();
               GL11.glPopMatrix();
               GL11.glTranslated(0.0, 0.0, e.isSneaking() ? 0.25 : 0.0);
               GL11.glPushMatrix();
               GL11.glTranslated(0.0, e.isSneaking() ? -0.05 : 0.0, e.isSneaking() ? -0.01725 : 0.0);
               GL11.glPushMatrix();
               GL11.glTranslated(-0.375, (double)yOff + 0.55, 0.0);
               if (entPos[1][0] != 0.0F) {
                  GL11.glRotatef(entPos[1][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }

               if (entPos[1][1] != 0.0F) {
                  GL11.glRotatef(entPos[1][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (entPos[1][2] != 0.0F) {
                  GL11.glRotatef(-entPos[1][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               GL11.glBegin(3);
               GL11.glVertex3d(0.0, 0.0, 0.0);
               GL11.glVertex3d(0.0, -0.5, 0.0);
               GL11.glEnd();
               GL11.glPopMatrix();
               GL11.glPushMatrix();
               GL11.glTranslated(0.375, (double)yOff + 0.55, 0.0);
               if (entPos[2][0] != 0.0F) {
                  GL11.glRotatef(entPos[2][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }

               if (entPos[2][1] != 0.0F) {
                  GL11.glRotatef(entPos[2][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (entPos[2][2] != 0.0F) {
                  GL11.glRotatef(-entPos[2][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               GL11.glBegin(3);
               GL11.glVertex3d(0.0, 0.0, 0.0);
               GL11.glVertex3d(0.0, -0.5, 0.0);
               GL11.glEnd();
               GL11.glPopMatrix();
               GL11.glRotatef(xOff - e.rotationYawHead, 0.0F, 1.0F, 0.0F);
               GL11.glPushMatrix();
               GL11.glTranslated(0.0, (double)yOff + 0.55, 0.0);
               if (entPos[0][0] != 0.0F) {
                  GL11.glRotatef(entPos[0][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }

               GL11.glBegin(3);
               GL11.glVertex3d(0.0, 0.0, 0.0);
               GL11.glVertex3d(0.0, 0.3, 0.0);
               GL11.glEnd();
               GL11.glPopMatrix();
               GL11.glPopMatrix();
               GL11.glRotatef(e.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
               GL11.glTranslated(0.0, e.isSneaking() ? -0.16175 : 0.0, e.isSneaking() ? -0.48025 : 0.0);
               GL11.glPushMatrix();
               GL11.glTranslated(0.0, (double)yOff, 0.0);
               GL11.glBegin(3);
               GL11.glVertex3d(-0.125, 0.0, 0.0);
               GL11.glVertex3d(0.125, 0.0, 0.0);
               GL11.glEnd();
               GL11.glPopMatrix();
               GL11.glPushMatrix();
               GL11.glTranslated(0.0, (double)yOff, 0.0);
               GL11.glBegin(3);
               GL11.glVertex3d(0.0, 0.0, 0.0);
               GL11.glVertex3d(0.0, 0.55, 0.0);
               GL11.glEnd();
               GL11.glPopMatrix();
               GL11.glPushMatrix();
               GL11.glTranslated(0.0, (double)yOff + 0.55, 0.0);
               GL11.glBegin(3);
               GL11.glVertex3d(-0.375, 0.0, 0.0);
               GL11.glVertex3d(0.375, 0.0, 0.0);
               GL11.glEnd();
               GL11.glPopMatrix();
               GL11.glPopMatrix();
            }
         }
      }
   }

   private void startEnd(boolean revert) {
      if (revert) {
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         GL11.glEnable(2848);
         GlStateManager.disableDepth();
         GlStateManager.disableTexture2D();
         GL11.glHint(3154, 4354);
      } else {
         GlStateManager.disableBlend();
         GlStateManager.enableTexture2D();
         GL11.glDisable(2848);
         GlStateManager.enableDepth();
         GlStateManager.popMatrix();
      }

      GlStateManager.depthMask(!revert);
   }

   private boolean doesntContain(EntityPlayer entityPlayer) {
      return !mc.world.playerEntities.contains(entityPlayer);
   }
}
