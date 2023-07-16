//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.gui.components;

import java.util.ArrayList;
import me.moon.Moon;
import me.moon.features.Feature;
import me.moon.features.gui.MoonGui;
import me.moon.features.gui.components.items.Item;
import me.moon.features.gui.components.items.buttons.Button;
import me.moon.features.modules.client.ClickGui;
import me.moon.util.ColorUtil;
import me.moon.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.SoundEvents;

public class Component extends Feature {
   public static int[] counter1 = new int[]{1};
   private final ArrayList<Item> items = new ArrayList<>();
   public boolean drag;
   private int x;
   private int y;
   private int x2;
   private int y2;
   private int width;
   private int height;
   private boolean open;
   private boolean hidden = false;

   public Component(String name, int x, int y, boolean open) {
      super(name);
      this.x = x;
      this.y = y;
      this.width = 88;
      this.height = 18;
      this.open = open;
      this.setupItems();
   }

   public void setupItems() {
   }

   private void drag(int mouseX, int mouseY) {
      if (this.drag) {
         this.x = this.x2 + mouseX;
         this.y = this.y2 + mouseY;
      }
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drag(mouseX, mouseY);
      counter1 = new int[]{1};
      float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0F : 0.0F;
      int color = ColorUtil.toARGB(
         ClickGui.getInstance().topRed.getValue(), ClickGui.getInstance().topGreen.getValue(), ClickGui.getInstance().topBlue.getValue(), 255
      );
      Gui.drawRect(
         this.x,
         this.y - 1,
         this.x + this.width,
         this.y + this.height - 6,
         ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB() : color
      );
      if (this.open) {
         RenderUtil.drawRect((float)this.x, (float)this.y + 12.5F, (float)(this.x + this.width), (float)(this.y + this.height) + totalItemHeight, 1996488704);
      }

      Moon.textManager.drawStringWithShadow(this.getName(), (float)this.x + 3.0F, (float)this.y - 4.0F - (float)MoonGui.getClickGui().getTextOffset(), -1);
      if (this.open) {
         float y = (float)(this.getY() + this.getHeight()) - 3.0F;

         for(Item item : this.getItems()) {
            counter1[0]++;
            if (!item.isHidden()) {
               item.setLocation((float)this.x + 2.0F, y);
               item.setWidth(this.getWidth() - 4);
               item.drawScreen(mouseX, mouseY, partialTicks);
               y += (float)item.getHeight() + 1.5F;
            }
         }
      }
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
         this.x2 = this.x - mouseX;
         this.y2 = this.y - mouseY;
         MoonGui.getClickGui().getComponents().forEach(component -> {
            if (component.drag) {
               component.drag = false;
            }
         });
         this.drag = true;
      } else if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
         this.open = !this.open;
         mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (this.open) {
         this.getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
      }
   }

   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
      if (releaseButton == 0) {
         this.drag = false;
      }

      if (this.open) {
         this.getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
      }
   }

   public void onKeyTyped(char typedChar, int keyCode) {
      if (this.open) {
         this.getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
      }
   }

   public void addButton(Button button) {
      this.items.add(button);
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   public boolean isOpen() {
      return this.open;
   }

   public final ArrayList<Item> getItems() {
      return this.items;
   }

   private boolean isHovering(int mouseX, int mouseY) {
      return mouseX >= this.getX()
         && mouseX <= this.getX() + this.getWidth()
         && mouseY >= this.getY()
         && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
   }

   private float getTotalItemHeight() {
      float height = 0.0F;

      for(Item item : this.getItems()) {
         height += (float)item.getHeight() + 1.5F;
      }

      return height;
   }
}
