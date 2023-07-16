//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.gui.components.items.buttons;

import me.moon.Moon;
import me.moon.features.gui.MoonGui;
import me.moon.features.gui.components.Component;
import me.moon.features.gui.components.items.Item;
import me.moon.features.modules.client.ClickGui;
import me.moon.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button extends Item {
   private boolean state;

   public Button(String name) {
      super(name);
      this.height = 15;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      RenderUtil.drawRect(
         this.x,
         this.y,
         this.x + (float)this.width,
         this.y + (float)this.height - 0.5F,
         this.getState()
            ? (
               !this.isHovering(mouseX, mouseY)
                  ? Moon.colorManager.getColorWithAlpha(Moon.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue())
                  : Moon.colorManager.getColorWithAlpha(Moon.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())
            )
            : (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515)
      );
      Moon.textManager
         .drawStringWithShadow(this.getName(), this.x + 2.3F, this.y - 2.0F - (float)MoonGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
         this.onMouseClick();
      }
   }

   public void onMouseClick() {
      this.state = !this.state;
      this.toggle();
      mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public void toggle() {
   }

   public boolean getState() {
      return this.state;
   }

   @Override
   public int getHeight() {
      return 14;
   }

   public boolean isHovering(int mouseX, int mouseY) {
      for(Component component : MoonGui.getClickGui().getComponents()) {
         if (component.drag) {
            return false;
         }
      }

      return (float)mouseX >= this.getX()
         && (float)mouseX <= this.getX() + (float)this.getWidth()
         && (float)mouseY >= this.getY()
         && (float)mouseY <= this.getY() + (float)this.height;
   }
}
