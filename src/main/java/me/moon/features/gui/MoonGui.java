//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import me.moon.Moon;
import me.moon.features.Feature;
import me.moon.features.gui.components.Component;
import me.moon.features.gui.components.items.Item;
import me.moon.features.gui.components.items.buttons.ModuleButton;
import me.moon.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class MoonGui extends GuiScreen {
   private static MoonGui INSTANCE = new MoonGui();
   private final ArrayList<Component> components = new ArrayList<>();

   public MoonGui() {
      this.setInstance();
      this.load();
   }

   public static MoonGui getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new MoonGui();
      }

      return INSTANCE;
   }

   public static MoonGui getClickGui() {
      return getInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   private void load() {
      int x = -84;

      for(final Module.Category category : Moon.moduleManager.getCategories()) {
         ArrayList var10000 = this.components;
         String var10004 = category.getName();
         x += 90;
         var10000.add(new Component(var10004, x, 4, true) {
            @Override
            public void setupItems() {
               counter1 = new int[]{1};
               Moon.moduleManager.getModulesByCategory(category).forEach(module -> {
                  if (!module.hidden) {
                     this.addButton(new ModuleButton(module));
                  }
               });
            }
         });
      }

      this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
   }

   public void updateModule(Module module) {
      for(Component component : this.components) {
         for(Item item : component.getItems()) {
            if (item instanceof ModuleButton) {
               ModuleButton button = (ModuleButton)item;
               Module mod = button.getModule();
               if (module != null && module.equals(mod)) {
                  button.initSettings();
               }
            }
         }
      }
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.checkMouseWheel();
      this.drawDefaultBackground();
      this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
   }

   public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
      this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
   }

   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
      this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public final ArrayList<Component> getComponents() {
      return this.components;
   }

   public void checkMouseWheel() {
      int dWheel = Mouse.getDWheel();
      if (dWheel < 0) {
         this.components.forEach(component -> component.setY(component.getY() - 10));
      } else if (dWheel > 0) {
         this.components.forEach(component -> component.setY(component.getY() + 10));
      }
   }

   public int getTextOffset() {
      return -6;
   }

   public Component getComponentByName(String name) {
      for(Component component : this.components) {
         if (component.getName().equalsIgnoreCase(name)) {
            return component;
         }
      }

      return null;
   }

   public void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
   }
}
