//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import me.moon.Moon;
import me.moon.event.events.Render2DEvent;
import me.moon.event.events.Render3DEvent;
import me.moon.features.Feature;
import me.moon.features.gui.MoonGui;
import me.moon.features.modules.Module;
import me.moon.features.modules.client.ClickGui;
import me.moon.features.modules.client.FontMod;
import me.moon.features.modules.client.HUD;
import me.moon.features.modules.combat.Auto32k;
import me.moon.features.modules.combat.AutoArmor;
import me.moon.features.modules.combat.AutoTrap;
import me.moon.features.modules.combat.AutoWeb;
import me.moon.features.modules.combat.Criticals;
import me.moon.features.modules.combat.FeetBreak;
import me.moon.features.modules.combat.HoleFiller;
import me.moon.features.modules.combat.Killaura;
import me.moon.features.modules.combat.MoonAura;
import me.moon.features.modules.combat.Offhand;
import me.moon.features.modules.combat.Selftrap;
import me.moon.features.modules.combat.Surround;
import me.moon.features.modules.misc.AutoGG;
import me.moon.features.modules.misc.ChatSuffix;
import me.moon.features.modules.misc.Interact;
import me.moon.features.modules.misc.MCF;
import me.moon.features.modules.misc.Message;
import me.moon.features.modules.misc.MultiTask;
import me.moon.features.modules.misc.NoHitBox;
import me.moon.features.modules.misc.PacketEat;
import me.moon.features.modules.misc.Timer;
import me.moon.features.modules.misc.ToolTips;
import me.moon.features.modules.movement.ElytraFlight;
import me.moon.features.modules.movement.NoFall;
import me.moon.features.modules.movement.PlayerTweaks;
import me.moon.features.modules.movement.ReverseStep;
import me.moon.features.modules.movement.Sprint;
import me.moon.features.modules.movement.Step;
import me.moon.features.modules.movement.Strafe;
import me.moon.features.modules.player.Anti32k;
import me.moon.features.modules.player.AutoXP;
import me.moon.features.modules.player.Blink;
import me.moon.features.modules.player.Burrow;
import me.moon.features.modules.player.FakePlayer;
import me.moon.features.modules.player.MoonBreak;
import me.moon.features.modules.player.Replenish;
import me.moon.features.modules.render.BlockHighlight;
import me.moon.features.modules.render.ESP;
import me.moon.features.modules.render.Fullbright;
import me.moon.features.modules.render.HoleESP;
import me.moon.features.modules.render.NameTags;
import me.moon.features.modules.render.NoRender;
import me.moon.features.modules.render.Skeleton;
import me.moon.features.modules.render.SurroundRender;
import me.moon.util.Util;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public class ModuleManager extends Feature {
   public ArrayList<Module> modules = new ArrayList<>();
   public List<Module> sortedModules = new ArrayList<>();
   public List<String> sortedModulesABC = new ArrayList<>();
   public ModuleManager.Animation animationThread;

   public void init() {
      this.modules.add(new ClickGui());
      this.modules.add(new FontMod());
      this.modules.add(new HUD());
      this.modules.add(new BlockHighlight());
      this.modules.add(new HoleESP());
      this.modules.add(new Skeleton());
      this.modules.add(new Replenish());
      this.modules.add(new FakePlayer());
      this.modules.add(new MoonBreak());
      this.modules.add(new Step());
      this.modules.add(new Interact());
      this.modules.add(new MCF());
      this.modules.add(new AutoGG());
      this.modules.add(new ToolTips());
      this.modules.add(new Offhand());
      this.modules.add(new Burrow());
      this.modules.add(new Surround());
      this.modules.add(new AutoTrap());
      this.modules.add(new AutoWeb());
      this.modules.add(new Fullbright());
      this.modules.add(new NoRender());
      this.modules.add(new MoonAura());
      this.modules.add(new Killaura());
      this.modules.add(new HoleFiller());
      this.modules.add(new AutoArmor());
      this.modules.add(new Sprint());
      this.modules.add(new ElytraFlight());
      this.modules.add(new MultiTask());
      this.modules.add(new ESP());
      this.modules.add(new Selftrap());
      this.modules.add(new NoHitBox());
      this.modules.add(new PlayerTweaks());
      this.modules.add(new NoFall());
      this.modules.add(new Criticals());
      this.modules.add(new AutoXP());
      this.modules.add(new Blink());
      this.modules.add(new Anti32k());
      this.modules.add(new ReverseStep());
      this.modules.add(new SurroundRender());
      this.modules.add(new FeetBreak());
      this.modules.add(new Message());
      this.modules.add(new PacketEat());
      this.modules.add(new Timer());
      this.modules.add(new ChatSuffix());
      this.modules.add(new Strafe());
      this.modules.add(new Auto32k());
      this.modules.add(new NameTags());
   }

   public Module getModuleByName(String name) {
      for(Module module : this.modules) {
         if (module.getName().equalsIgnoreCase(name)) {
            return module;
         }
      }

      return null;
   }

   public <T extends Module> T getModuleByClass(Class<T> clazz) {
      for(Module module : this.modules) {
         if (clazz.isInstance(module)) {
            return (T)module;
         }
      }

      return null;
   }

   public void enableModule(Class<Module> clazz) {
      Module module = this.getModuleByClass(clazz);
      if (module != null) {
         module.enable();
      }
   }

   public void disableModule(Class<Module> clazz) {
      Module module = this.getModuleByClass(clazz);
      if (module != null) {
         module.disable();
      }
   }

   public void enableModule(String name) {
      Module module = this.getModuleByName(name);
      if (module != null) {
         module.enable();
      }
   }

   public void disableModule(String name) {
      Module module = this.getModuleByName(name);
      if (module != null) {
         module.disable();
      }
   }

   public boolean isModuleEnabled(String name) {
      Module module = this.getModuleByName(name);
      return module != null && module.isOn();
   }

   public boolean isModuleEnabled(Class<Module> clazz) {
      Module module = this.getModuleByClass(clazz);
      return module != null && module.isOn();
   }

   public Module getModuleByDisplayName(String displayName) {
      for(Module module : this.modules) {
         if (module.getDisplayName().equalsIgnoreCase(displayName)) {
            return module;
         }
      }

      return null;
   }

   public ArrayList<Module> getEnabledModules() {
      ArrayList<Module> enabledModules = new ArrayList<>();

      for(Module module : this.modules) {
         if (module.isEnabled()) {
            enabledModules.add(module);
         }
      }

      return enabledModules;
   }

   public ArrayList<String> getEnabledModulesName() {
      ArrayList<String> enabledModules = new ArrayList<>();

      for(Module module : this.modules) {
         if (module.isEnabled() && module.isDrawn()) {
            enabledModules.add(module.getFullArrayString());
         }
      }

      return enabledModules;
   }

   public ArrayList<Module> getModulesByCategory(Module.Category category) {
      ArrayList<Module> modulesCategory = new ArrayList<>();
      this.modules.forEach(module -> {
         if (module.getCategory() == category) {
            modulesCategory.add(module);
         }
      });
      return modulesCategory;
   }

   public List<Module.Category> getCategories() {
      return Arrays.asList(Module.Category.values());
   }

   public void onLoad() {
      this.modules.stream().filter(Module::listening).forEach(MinecraftForge.EVENT_BUS::register);
      this.modules.forEach(Module::onLoad);
   }

   public void onUpdate() {
      this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
   }

   public void onTick() {
      this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
   }

   public void onRender2D(Render2DEvent event) {
      this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
   }

   public void onRender3D(Render3DEvent event) {
      this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
   }

   public void sortModules(boolean reverse) {
      this.sortedModules = this.getEnabledModules()
         .stream()
         .filter(Module::isDrawn)
         .sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1)))
         .collect(Collectors.toList());
   }

   public void sortModulesABC() {
      this.sortedModulesABC = new ArrayList<>(this.getEnabledModulesName());
      this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
   }

   public void onLogout() {
      this.modules.forEach(Module::onLogout);
   }

   public void onLogin() {
      this.modules.forEach(Module::onLogin);
   }

   public void onUnload() {
      this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
      this.modules.forEach(Module::onUnload);
   }

   public void onUnloadPost() {
      for(Module module : this.modules) {
         module.enabled.setValue(false);
      }
   }

   public void onKeyPressed(int eventKey) {
      if (eventKey != 0 && Keyboard.getEventKeyState() && !(mc.currentScreen instanceof MoonGui)) {
         this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
               module.toggle();
            }
         });
      }
   }

   private class Animation extends Thread {
      ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

      public Animation() {
         super("Animation");
      }

      @Override
      public void run() {
         if (HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.Length) {
            for(Module module : ModuleManager.this.sortedModules) {
               String text = module.getDisplayName()
                  + ChatFormatting.GRAY
                  + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
               module.offset = (float)ModuleManager.this.renderer.getStringWidth(text) / HUD.getInstance().animationHorizontalTime.getValue().floatValue();
               module.vOffset = (float)ModuleManager.this.renderer.getFontHeight() / HUD.getInstance().animationVerticalTime.getValue().floatValue();
               if (module.isEnabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                  if (module.arrayListOffset > module.offset && Util.mc.world != null) {
                     module.arrayListOffset -= module.offset;
                     module.sliding = true;
                  }
               } else if (module.isDisabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                  if (module.arrayListOffset < (float)ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                     module.arrayListOffset += module.offset;
                     module.sliding = true;
                  } else {
                     module.sliding = false;
                  }
               }
            }
         } else {
            for(String e : ModuleManager.this.sortedModulesABC) {
               Module module = Moon.moduleManager.getModuleByName(e);
               String text = module.getDisplayName()
                  + ChatFormatting.GRAY
                  + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
               module.offset = (float)ModuleManager.this.renderer.getStringWidth(text) / HUD.getInstance().animationHorizontalTime.getValue().floatValue();
               module.vOffset = (float)ModuleManager.this.renderer.getFontHeight() / HUD.getInstance().animationVerticalTime.getValue().floatValue();
               if (module.isEnabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                  if (module.arrayListOffset > module.offset && Util.mc.world != null) {
                     module.arrayListOffset -= module.offset;
                     module.sliding = true;
                  }
               } else if (module.isDisabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                  if (module.arrayListOffset < (float)ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                     module.arrayListOffset += module.offset;
                     module.sliding = true;
                  } else {
                     module.sliding = false;
                  }
               }
            }
         }
      }

      @Override
      public void start() {
         System.out.println("Starting animation thread.");
         this.service.scheduleAtFixedRate(this, 0L, 1L, TimeUnit.MILLISECONDS);
      }
   }
}
