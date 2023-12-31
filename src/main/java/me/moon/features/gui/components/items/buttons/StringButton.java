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
import net.minecraft.util.ChatAllowedCharacters;

public class StringButton extends Button {
   private final Setting setting;
   public boolean isListening;
   private StringButton.CurrentString currentString = new StringButton.CurrentString("");

   public StringButton(Setting setting) {
      super(setting.getName());
      this.setting = setting;
      this.width = 15;
   }

   public static String removeLastChar(String str) {
      String output = "";
      if (str != null && str.length() > 0) {
         output = str.substring(0, str.length() - 1);
      }

      return output;
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
      if (this.isListening) {
         Moon.textManager
            .drawStringWithShadow(
               this.currentString.getString() + Moon.textManager.getIdleSign(),
               this.x + 2.3F,
               this.y - 1.7F - (float)MoonGui.getClickGui().getTextOffset(),
               this.getState() ? -1 : -5592406
            );
      } else {
         Moon.textManager
            .drawStringWithShadow(
               (this.setting.getName().equals("Buttons") ? "Buttons " : (this.setting.getName().equals("Prefix") ? "Prefix  " + ChatFormatting.GRAY : ""))
                  + this.setting.getValue(),
               this.x + 2.3F,
               this.y - 1.7F - (float)MoonGui.getClickGui().getTextOffset(),
               this.getState() ? -1 : -5592406
            );
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isHovering(mouseX, mouseY)) {
         mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }
   }

   @Override
   public void onKeyTyped(char typedChar, int keyCode) {
      if (this.isListening) {
         switch(keyCode) {
            case 1:
               return;
            case 28:
               this.enterString();
            case 14:
               this.setString(removeLastChar(this.currentString.getString()));
            default:
               if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                  this.setString(this.currentString.getString() + typedChar);
               }
         }
      }
   }

   @Override
   public void update() {
      this.setHidden(!this.setting.isVisible());
   }

   private void enterString() {
      if (this.currentString.getString().isEmpty()) {
         this.setting.setValue(this.setting.getDefaultValue());
      } else {
         this.setting.setValue(this.currentString.getString());
      }

      this.setString("");
      this.onMouseClick();
   }

   @Override
   public int getHeight() {
      return 14;
   }

   @Override
   public void toggle() {
      this.isListening = !this.isListening;
   }

   @Override
   public boolean getState() {
      return !this.isListening;
   }

   public void setString(String newString) {
      this.currentString = new StringButton.CurrentString(newString);
   }

   public static class CurrentString {
      private final String string;

      public CurrentString(String string) {
         this.string = string;
      }

      public String getString() {
         return this.string;
      }
   }
}
