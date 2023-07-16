//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.manager;

import java.awt.Font;
import me.moon.Moon;
import me.moon.features.Feature;
import me.moon.features.gui.font.CustomFont;
import me.moon.features.modules.client.FontMod;
import me.moon.util.Timer;
import net.minecraft.util.math.MathHelper;

public class TextManager extends Feature {
   private final Timer idleTimer = new Timer();
   public int scaledWidth;
   public int scaledHeight;
   public int scaleFactor;
   private CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, false);
   private boolean idling;

   public TextManager() {
      this.updateResolution();
   }

   public void init(boolean startup) {
      FontMod cFont = Moon.moduleManager.getModuleByClass(FontMod.class);

      try {
         this.setFontRenderer(
            new Font(cFont.fontName.getValue(), cFont.fontStyle.getValue(), cFont.fontSize.getValue()),
            cFont.antiAlias.getValue(),
            cFont.fractionalMetrics.getValue()
         );
      } catch (Exception var4) {
      }
   }

   public void drawStringWithShadow(String text, float x, float y, int color) {
      this.drawString(text, x, y, color, true);
   }

   public void drawString(String text, float x, float y, int color, boolean shadow) {
      if (Moon.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
         if (shadow) {
            this.customFont.drawStringWithShadow(text, (double)x, (double)y, color);
         } else {
            this.customFont.drawString(text, x, y, color);
         }
      } else {
         mc.fontRenderer.drawString(text, x, y, color, shadow);
      }
   }

   public int getStringWidth(String text) {
      return Moon.moduleManager.isModuleEnabled(FontMod.getInstance().getName()) ? this.customFont.getStringWidth(text) : mc.fontRenderer.getStringWidth(text);
   }

   public int getFontHeight() {
      if (Moon.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
         String text = "A";
         return this.customFont.getStringHeight(text);
      } else {
         return mc.fontRenderer.FONT_HEIGHT;
      }
   }

   public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
      this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
   }

   public Font getCurrentFont() {
      return this.customFont.getFont();
   }

   public void updateResolution() {
      this.scaledWidth = mc.displayWidth;
      this.scaledHeight = mc.displayHeight;
      this.scaleFactor = 1;
      boolean flag = mc.isUnicode();
      int i = mc.gameSettings.guiScale;
      if (i == 0) {
         i = 1000;
      }

      while(this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
         ++this.scaleFactor;
      }

      if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
         --this.scaleFactor;
      }

      double scaledWidthD = (double)(this.scaledWidth / this.scaleFactor);
      double scaledHeightD = (double)(this.scaledHeight / this.scaleFactor);
      this.scaledWidth = MathHelper.ceil(scaledWidthD);
      this.scaledHeight = MathHelper.ceil(scaledHeightD);
   }

   public String getIdleSign() {
      if (this.idleTimer.passedMs(500L)) {
         this.idling = !this.idling;
         this.idleTimer.reset();
      }

      return this.idling ? "_" : "";
   }
}
