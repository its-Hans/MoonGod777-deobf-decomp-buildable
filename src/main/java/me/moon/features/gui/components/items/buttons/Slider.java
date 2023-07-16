package me.moon.features.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.moon.Moon;
import me.moon.features.gui.MoonGui;
import me.moon.features.gui.components.Component;
import me.moon.features.modules.client.ClickGui;
import me.moon.features.setting.Setting;
import me.moon.util.RenderUtil;
import org.lwjgl.input.Mouse;

public class Slider extends Button {
   private final Number min;
   private final Number max;
   private final int difference;
   public Setting setting;

   public Slider(Setting setting) {
      super(setting.getName());
      this.setting = setting;
      this.min = (Number)setting.getMin();
      this.max = (Number)setting.getMax();
      this.difference = this.max.intValue() - this.min.intValue();
      this.width = 15;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.dragSetting(mouseX, mouseY);
      RenderUtil.drawRect(
         this.x, this.y, this.x + (float)this.width + 7.4F, this.y + (float)this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515
      );
      RenderUtil.drawRect(
         this.x,
         this.y,
         ((Number)this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x : this.x + ((float)this.width + 7.4F) * this.partialMultiplier(),
         this.y + (float)this.height - 0.5F,
         !this.isHovering(mouseX, mouseY)
            ? Moon.colorManager.getColorWithAlpha(Moon.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue())
            : Moon.colorManager.getColorWithAlpha(Moon.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())
      );
      Moon.textManager
         .drawStringWithShadow(
            this.getName()
               + " "
               + ChatFormatting.GRAY
               + (this.setting.getValue() instanceof Float ? this.setting.getValue() : ((Number)this.setting.getValue()).doubleValue()),
            this.x + 2.3F,
            this.y - 1.7F - (float)MoonGui.getClickGui().getTextOffset(),
            -1
         );
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isHovering(mouseX, mouseY)) {
         this.setSettingFromX(mouseX);
      }
   }

   @Override
   public boolean isHovering(int mouseX, int mouseY) {
      for(Component component : MoonGui.getClickGui().getComponents()) {
         if (component.drag) {
            return false;
         }
      }

      return (float)mouseX >= this.getX()
         && (float)mouseX <= this.getX() + (float)this.getWidth() + 8.0F
         && (float)mouseY >= this.getY()
         && (float)mouseY <= this.getY() + (float)this.height;
   }

   @Override
   public void update() {
      this.setHidden(!this.setting.isVisible());
   }

   private void dragSetting(int mouseX, int mouseY) {
      if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
         this.setSettingFromX(mouseX);
      }
   }

   @Override
   public int getHeight() {
      return 14;
   }

   private void setSettingFromX(int mouseX) {
      float percent = ((float)mouseX - this.x) / ((float)this.width + 7.4F);
      if (this.setting.getValue() instanceof Double) {
         Setting<Double> it = this.setting;
         double result = it.getMin() + (double)((float)this.difference * percent);
         this.setting.setValue((double)Math.round(10.0 * result) / 10.0);
      } else if (this.setting.getValue() instanceof Float) {
         Setting<Float> it = this.setting;
         float result = it.getMin() + (float)this.difference * percent;
         this.setting.setValue((float)Math.round(10.0F * result) / 10.0F);
      } else if (this.setting.getValue() instanceof Integer) {
         Setting<Integer> it = this.setting;
         this.setting.setValue(it.getMin() + (int)((float)this.difference * percent));
      }
   }

   private float middle() {
      return this.max.floatValue() - this.min.floatValue();
   }

   private float part() {
      return ((Number)this.setting.getValue()).floatValue() - this.min.floatValue();
   }

   private float partialMultiplier() {
      return this.part() / this.middle();
   }
}
