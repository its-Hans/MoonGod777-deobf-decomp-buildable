package me.moon;

import me.moon.manager.ColorManager;
import me.moon.manager.CommandManager;
import me.moon.manager.ConfigManager;
import me.moon.manager.EventManager;
import me.moon.manager.FileManager;
import me.moon.manager.FriendManager;
import me.moon.manager.HoleManager;
import me.moon.manager.InventoryManager;
import me.moon.manager.ModuleManager;
import me.moon.manager.PacketManager;
import me.moon.manager.PositionManager;
import me.moon.manager.PotionManager;
import me.moon.manager.ReloadManager;
import me.moon.manager.RotationManager;
import me.moon.manager.ServerManager;
import me.moon.manager.SpeedManager;
import me.moon.manager.TextManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(
   modid = "moon",
   name = "Moon",
   version = "1.0.0+"
)
public class Moon {
   public static final String MODID = "moon";
   public static final String MODNAME = "MoonGod";
   public static final String MODVER = "777";
   public static final Logger LOGGER = LogManager.getLogger("MoonGod");
   public static CommandManager commandManager;
   public static FriendManager friendManager;
   public static ModuleManager moduleManager;
   public static PacketManager packetManager;
   public static ColorManager colorManager;
   public static HoleManager holeManager;
   public static InventoryManager inventoryManager;
   public static PotionManager potionManager;
   public static RotationManager rotationManager;
   public static PositionManager positionManager;
   public static SpeedManager speedManager;
   public static ReloadManager reloadManager;
   public static FileManager fileManager;
   public static ConfigManager configManager;
   public static ServerManager serverManager;
   public static EventManager eventManager;
   public static TextManager textManager;
   @Instance
   public static Moon INSTANCE;
   private static boolean unloaded = false;

   public static void load() {
      LOGGER.info("\n\nLoading Moon by 404\n\n");
      unloaded = false;
      if (reloadManager != null) {
         reloadManager.unload();
         reloadManager = null;
      }

      textManager = new TextManager();
      commandManager = new CommandManager();
      friendManager = new FriendManager();
      moduleManager = new ModuleManager();
      rotationManager = new RotationManager();
      packetManager = new PacketManager();
      eventManager = new EventManager();
      speedManager = new SpeedManager();
      potionManager = new PotionManager();
      inventoryManager = new InventoryManager();
      serverManager = new ServerManager();
      fileManager = new FileManager();
      colorManager = new ColorManager();
      positionManager = new PositionManager();
      configManager = new ConfigManager();
      holeManager = new HoleManager();
      LOGGER.info("\n\nManagers loaded\n\n");
      moduleManager.init();
      LOGGER.info("\n\nModules loaded\n\n");
      configManager.init();
      eventManager.init();
      LOGGER.info("\n\nEventManager loaded\n\n");
      textManager.init(true);
      moduleManager.onLoad();
      LOGGER.info("\n\nMoon successfully loaded!\n\n");
   }

   public static void unload(boolean unload) {
      LOGGER.info("\n\nUnloading Moon by 404\n\n");
      if (unload) {
         reloadManager = new ReloadManager();
         reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
      }

      onUnload();
      eventManager = null;
      friendManager = null;
      speedManager = null;
      holeManager = null;
      positionManager = null;
      rotationManager = null;
      configManager = null;
      commandManager = null;
      colorManager = null;
      serverManager = null;
      fileManager = null;
      potionManager = null;
      inventoryManager = null;
      moduleManager = null;
      textManager = null;
      LOGGER.info("\nMoon unloaded!\n");
   }

   public static void reload() {
      unload(false);
      load();
   }

   public static void onUnload() {
      if (!unloaded) {
         eventManager.onUnload();
         moduleManager.onUnload();
         configManager.saveConfig(configManager.config.replaceFirst("moon/", ""));
         moduleManager.onUnloadPost();
         unloaded = true;
      }
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      LOGGER.info("\n\n4 0 4\n\n");
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      Display.setTitle("MoonGod");
      load();
   }
}
