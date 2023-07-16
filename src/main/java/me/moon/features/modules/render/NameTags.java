//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Objects;
import me.moon.Moon;
import me.moon.event.events.Render3DEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.RenderUtil;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class NameTags extends Module {
   private final Setting<Boolean> invisibles = this.register(new Setting<>("Invisibles", true));

   public NameTags() {
      super("NameTags", "Better Nametags", Module.Category.RENDER, false, false, false);
   }

   @Override
   public void onRender3D(Render3DEvent event) {
      if (!fullNullCheck()) {
         for(EntityPlayer player : mc.world.playerEntities) {
            if (!((double)player.getDistance(mc.player) > 30.0)
               && player != null
               && !player.equals(mc.player)
               && player.isEntityAlive()
               && (!player.isInvisible() || this.invisibles.getValue())) {
               double x = this.interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - mc.getRenderManager().viewerPosX;
               double y = this.interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - mc.getRenderManager().viewerPosY;
               double z = this.interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - mc.getRenderManager().viewerPosZ;
               this.renderNameTag(player, x, y, z, event.getPartialTicks());
            }
         }
      }
   }

   private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
      double tempY = y + (player.isSneaking() ? 0.5 : 0.7);
      Entity camera = mc.getRenderViewEntity();

      assert camera != null;

      double originalPositionX = camera.posX;
      double originalPositionY = camera.posY;
      double originalPositionZ = camera.posZ;
      camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
      camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
      camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
      String displayTag = this.getDisplayTag(player);
      double distance = camera.getDistance(
         x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ
      );
      int width = this.renderer.getStringWidth(displayTag) / 2;
      double scale = distance <= 6.0 ? 0.0245 : (0.0018 + 4.5 * distance * 1.0) / 1000.0;
      GlStateManager.pushMatrix();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.enablePolygonOffset();
      GlStateManager.doPolygonOffset(1.0F, -1500000.0F);
      GlStateManager.disableLighting();
      GlStateManager.translate((float)x, (float)tempY + 1.4F, (float)z);
      GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
      float var10001 = mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
      GlStateManager.rotate(mc.getRenderManager().playerViewX, var10001, 0.0F, 0.0F);
      GlStateManager.scale(-scale, -scale, scale);
      GlStateManager.disableDepth();
      GlStateManager.enableBlend();
      GlStateManager.enableBlend();
      RenderUtil.drawRect((float)(-width - 2), (float)(-(this.renderer.getFontHeight() + 1)), (float)width + 2.0F, 1.5F, 1426063360);
      GlStateManager.disableBlend();
      ItemStack renderMainHand = player.getHeldItemMainhand().copy();
      GlStateManager.pushMatrix();
      int xOffset = -8;

      for(ItemStack stack : player.inventory.armorInventory) {
         if (stack != null) {
            xOffset -= 8;
         }
      }

      xOffset -= 8;
      ItemStack renderOffhand = player.getHeldItemOffhand().copy();
      this.renderItemStack(renderOffhand, xOffset, -26);
      xOffset += 16;

      for(ItemStack stack2 : player.inventory.armorInventory) {
         if (stack2 != null) {
            ItemStack armourStack = stack2.copy();
            this.renderItemStack(armourStack, xOffset, -26);
            xOffset += 16;
         }
      }

      this.renderItemStack(renderMainHand, xOffset, -26);
      GlStateManager.popMatrix();
      this.renderer.drawStringWithShadow(displayTag, (float)(-width), (float)(-(this.renderer.getFontHeight() - 1)), this.getDisplayColour(player));
      camera.posX = originalPositionX;
      camera.posY = originalPositionY;
      camera.posZ = originalPositionZ;
      GlStateManager.enableDepth();
      GlStateManager.disableBlend();
      GlStateManager.disablePolygonOffset();
      GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
      GlStateManager.popMatrix();
   }

   private void renderItemStack(ItemStack stack, int x, int y) {
      GlStateManager.pushMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.clear(256);
      RenderHelper.enableStandardItemLighting();
      mc.getRenderItem().zLevel = -150.0F;
      GlStateManager.disableAlpha();
      GlStateManager.enableDepth();
      GlStateManager.disableCull();
      mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
      mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, y);
      mc.getRenderItem().zLevel = 0.0F;
      RenderHelper.disableStandardItemLighting();
      GlStateManager.enableCull();
      GlStateManager.enableAlpha();
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      GlStateManager.disableDepth();
      this.renderEnchantmentText(stack, x, y);
      GlStateManager.enableDepth();
      GlStateManager.scale(2.0F, 2.0F, 2.0F);
      GlStateManager.popMatrix();
   }

   private void renderEnchantmentText(ItemStack stack, int x, int y) {
      int enchantmentY = y - 8;
      if (stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
         this.renderer.drawStringWithShadow("God", (float)(x * 2), (float)enchantmentY, -3977919);
         enchantmentY -= 8;
      }

      if (stack.getItem() instanceof ItemArmor
         || stack.getItem() instanceof ItemSword
         || stack.getItem() instanceof ItemTool
         || stack.getItem() instanceof ItemShield) {
         int dmg = 0;
         float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
         float red = 1.0F - green;
         dmg = 100 - (int)(red * 100.0F);
         ChatFormatting color;
         if (dmg >= 60) {
            color = ChatFormatting.GREEN;
         } else if (dmg >= 25) {
            color = ChatFormatting.YELLOW;
         } else {
            color = ChatFormatting.RED;
         }

         this.renderer.drawStringWithShadow(color + "" + dmg + "%", (float)(x * 2), (float)enchantmentY, -1);
      }
   }

   private String getDisplayTag(EntityPlayer player) {
      String name = player.getDisplayName().getFormattedText();
      if (name.contains(mc.getSession().getUsername())) {
         name = "You";
      }

      float health = player.getHealth() + player.getAbsorptionAmount();
      ChatFormatting color;
      if (health > 18.0F) {
         color = ChatFormatting.GREEN;
      } else if (health > 16.0F) {
         color = ChatFormatting.DARK_GREEN;
      } else if (health > 12.0F) {
         color = ChatFormatting.YELLOW;
      } else if (health > 8.0F) {
         color = ChatFormatting.GOLD;
      } else if (health > 5.0F) {
         color = ChatFormatting.RED;
      } else {
         color = ChatFormatting.DARK_RED;
      }

      String pingStr = "";

      try {
         int responseTime = ((NetHandlerPlayClient)Objects.requireNonNull(mc.getConnection())).getPlayerInfo(player.getUniqueID()).getResponseTime();
         pingStr = pingStr + responseTime + "ms ";
      } catch (Exception var7) {
      }

      if (Math.floor((double)health) == (double)health) {
         name = name + color + ' ' + (health > 0.0F ? (int)Math.floor((double)health) : "dead");
      } else {
         name = name + color + ' ' + (health > 0.0F ? (int)health : "dead");
      }

      return pingStr + name;
   }

   private int getDisplayColour(EntityPlayer player) {
      int colour = -1;
      if (Moon.friendManager.isFriend(player)) {
         return -11157267;
      } else {
         if (player.isInvisible()) {
            colour = -1113785;
         } else if (player.isSneaking()) {
            colour = -6481515;
         }

         return colour;
      }
   }

   private double interpolate(double previous, double current, float delta) {
      return previous + (current - previous) * (double)delta;
   }
}
