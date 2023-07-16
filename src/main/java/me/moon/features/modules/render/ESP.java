//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.render;

import java.awt.Color;
import me.moon.event.events.Render3DEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.EntityUtil;
import me.moon.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class ESP extends Module {
   private static ESP INSTANCE = new ESP();
   private final Setting<Boolean> items = this.register(new Setting<>("Items", false));
   private final Setting<Boolean> pearl = this.register(new Setting<>("Pearls", false));
   private final Setting<Integer> red = this.register(new Setting<>("Red", 255, 0, 255));
   private final Setting<Integer> green = this.register(new Setting<>("Green", 255, 0, 255));
   private final Setting<Integer> blue = this.register(new Setting<>("Blue", 255, 0, 255));
   private final Setting<Integer> boxAlpha = this.register(new Setting<>("BoxAlpha", 120, 0, 255));
   private final Setting<Integer> alpha = this.register(new Setting<>("Alpha", 255, 0, 255));

   public ESP() {
      super("ESP", "Renders a nice ESP.", Module.Category.RENDER, false, false, false);
      this.setInstance();
   }

   public static ESP getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ESP();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @Override
   public void onRender3D(Render3DEvent event) {
      if (this.items.getValue()) {
         int i = 0;

         for(Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityItem && mc.player.getDistanceSq(entity) < 2500.0) {
               Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
               AxisAlignedBB bb = new AxisAlignedBB(
                  entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x,
                  entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y,
                  entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z,
                  entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x,
                  entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y,
                  entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z
               );
               GlStateManager.pushMatrix();
               GlStateManager.enableBlend();
               GlStateManager.disableDepth();
               GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
               GlStateManager.disableTexture2D();
               GlStateManager.depthMask(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.renderFilledBox(
                  bb,
                  (float)this.red.getValue().intValue() / 255.0F,
                  (float)this.green.getValue().intValue() / 255.0F,
                  (float)this.blue.getValue().intValue() / 255.0F,
                  (float)this.boxAlpha.getValue().intValue() / 255.0F
               );
               GL11.glDisable(2848);
               GlStateManager.depthMask(true);
               GlStateManager.enableDepth();
               GlStateManager.enableTexture2D();
               GlStateManager.disableBlend();
               GlStateManager.popMatrix();
               RenderUtil.drawBlockOutline(bb, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0F);
               if (++i >= 50) {
                  break;
               }
            }
         }
      }

      if (this.pearl.getValue()) {
         int i = 0;

         for(Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderPearl && mc.player.getDistanceSq(entity) < 2500.0) {
               Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
               AxisAlignedBB bb = new AxisAlignedBB(
                  entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x,
                  entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y,
                  entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z,
                  entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x,
                  entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y,
                  entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z
               );
               GlStateManager.pushMatrix();
               GlStateManager.enableBlend();
               GlStateManager.disableDepth();
               GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
               GlStateManager.disableTexture2D();
               GlStateManager.depthMask(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.renderFilledBox(
                  bb,
                  (float)this.red.getValue().intValue() / 255.0F,
                  (float)this.green.getValue().intValue() / 255.0F,
                  (float)this.blue.getValue().intValue() / 255.0F,
                  (float)this.boxAlpha.getValue().intValue() / 255.0F
               );
               GL11.glDisable(2848);
               GlStateManager.depthMask(true);
               GlStateManager.enableDepth();
               GlStateManager.enableTexture2D();
               GlStateManager.disableBlend();
               GlStateManager.popMatrix();
               RenderUtil.drawBlockOutline(bb, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0F);
               if (++i >= 50) {
                  break;
               }
            }
         }
      }
   }
}
