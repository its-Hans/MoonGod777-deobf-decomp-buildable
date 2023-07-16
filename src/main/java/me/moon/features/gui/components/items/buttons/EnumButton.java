//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.moon.Moon;
import me.moon.features.gui.MoonGui;
import me.moon.features.modules.client.ClickGui;
import me.moon.features.setting.Setting;
import me.moon.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class EnumButton extends Button {
   public Setting setting;

   public EnumButton(Setting setting) {
      super(setting.getName());
      this.setting = setting;
      this.width = 15;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      RenderUtil.drawRect(
         this.x,
         this.y,
         this.x + (float)this.width + 7.4F,
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
         .drawStringWithShadow(
            this.setting.getName()
               + " "
               + ChatFormatting.GRAY
               + (this.setting.currentEnumName().equalsIgnoreCase("ABC") ? "ABC" : this.setting.currentEnumName()),
            this.x + 2.3F,
            this.y - 1.7F - (float)MoonGui.getClickGui().getTextOffset(),
            this.getState() ? -1 : -5592406
         );
   }

   @Override
   public void update() {
      this.setHidden(!this.setting.isVisible());
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isHovering(mouseX, mouseY)) {
         mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }
   }

   @Override
   public int getHeight() {
      return 14;
   }

   @Override
   public void toggle() {
      this.setting.increaseEnum();
   }

   @Override
   public boolean getState() {
      return true;
   }
}
