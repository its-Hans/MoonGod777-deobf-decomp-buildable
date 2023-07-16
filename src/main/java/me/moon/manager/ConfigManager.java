package me.moon.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.moon.Moon;
import me.moon.features.Feature;
import me.moon.features.modules.Module;
import me.moon.features.setting.Bind;
import me.moon.features.setting.EnumConverter;
import me.moon.features.setting.Setting;
import me.moon.util.Util;

public class ConfigManager implements Util {
   public ArrayList<Feature> features = new ArrayList<>();
   public String config = "moon/config/";

   public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
      String var4 = setting.getType();
      switch(var4) {
         case "Boolean":
            setting.setValue(element.getAsBoolean());
            return;
         case "Double":
            setting.setValue(element.getAsDouble());
            return;
         case "Float":
            setting.setValue(element.getAsFloat());
            return;
         case "Integer":
            setting.setValue(element.getAsInt());
            return;
         case "String":
            String str = element.getAsString();
            setting.setValue(str.replace("_", " "));
            return;
         case "Bind":
            setting.setValue(new Bind.BindConverter().doBackward(element));
            return;
         case "Enum":
            try {
               EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
               Enum value = converter.doBackward(element);
               setting.setValue(value == null ? setting.getDefaultValue() : value);
            } catch (Exception var8) {
            }

            return;
         default:
            Moon.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
      }
   }

   private static void loadFile(JsonObject input, Feature feature) {
      for(Entry<String, JsonElement> entry : input.entrySet()) {
         String settingName = entry.getKey();
         JsonElement element = (JsonElement)entry.getValue();
         if (feature instanceof FriendManager) {
            try {
               Moon.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         } else {
            boolean settingFound = false;

            for(Setting setting : feature.getSettings()) {
               if (settingName.equals(setting.getName())) {
                  try {
                     setValueFromJson(feature, setting, element);
                  } catch (Exception var11) {
                     var11.printStackTrace();
                  }

                  settingFound = true;
               }
            }

            if (settingFound) {
            }
         }
      }
   }

   public void loadConfig(String name) {
      List<File> files = Arrays.stream(Objects.requireNonNull(new File("moon").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
      if (files.contains(new File("moon/" + name + "/"))) {
         this.config = "moon/" + name + "/";
      } else {
         this.config = "moon/config/";
      }

      Moon.friendManager.onLoad();

      for(Feature feature : this.features) {
         try {
            this.loadSettings(feature);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      this.saveCurrentConfig();
   }

   public boolean configExists(String name) {
      List<File> files = Arrays.stream(Objects.requireNonNull(new File("moon").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
      return files.contains(new File("moon/" + name + "/"));
   }

   public void saveConfig(String name) {
      this.config = "moon/" + name + "/";
      File path = new File(this.config);
      if (!path.exists()) {
         path.mkdir();
      }

      Moon.friendManager.saveFriends();

      for(Feature feature : this.features) {
         try {
            this.saveSettings(feature);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      this.saveCurrentConfig();
   }

   public void saveCurrentConfig() {
      File currentConfig = new File("moon/currentconfig.txt");

      try {
         if (currentConfig.exists()) {
            FileWriter writer = new FileWriter(currentConfig);
            String tempConfig = this.config.replaceAll("/", "");
            writer.write(tempConfig.replaceAll("moon", ""));
            writer.close();
         } else {
            currentConfig.createNewFile();
            FileWriter writer = new FileWriter(currentConfig);
            String tempConfig = this.config.replaceAll("/", "");
            writer.write(tempConfig.replaceAll("moon", ""));
            writer.close();
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   public String loadCurrentConfig() {
      File currentConfig = new File("moon/currentconfig.txt");
      String name = "config";

      try {
         if (currentConfig.exists()) {
            Scanner reader = new Scanner(currentConfig);

            while(reader.hasNextLine()) {
               name = reader.nextLine();
            }

            reader.close();
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return name;
   }

   public void resetConfig(boolean saveConfig, String name) {
      for(Feature feature : this.features) {
         feature.reset();
      }

      if (saveConfig) {
         this.saveConfig(name);
      }
   }

   public void saveSettings(Feature feature) throws IOException {
      new JsonObject();
      File directory = new File(this.config + this.getDirectory(feature));
      if (!directory.exists()) {
         directory.mkdir();
      }

      String featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json";
      Path outputFile = Paths.get(featureName);
      if (!Files.exists(outputFile)) {
         Files.createFile(outputFile);
      }

      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      String json = gson.toJson(this.writeSettings(feature));
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
      writer.write(json);
      writer.close();
   }

   public void init() {
      this.features.addAll(Moon.moduleManager.modules);
      this.features.add(Moon.friendManager);
      String name = this.loadCurrentConfig();
      this.loadConfig(name);
      Moon.LOGGER.info("Config loaded.");
   }

   private void loadSettings(Feature feature) throws IOException {
      String featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json";
      Path featurePath = Paths.get(featureName);
      if (Files.exists(featurePath)) {
         this.loadPath(featurePath, feature);
      }
   }

   private void loadPath(Path path, Feature feature) throws IOException {
      InputStream stream = Files.newInputStream(path);

      try {
         loadFile(new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
      } catch (IllegalStateException var5) {
         Moon.LOGGER.error("Bad Config File for: " + feature.getName() + ". Resetting...");
         loadFile(new JsonObject(), feature);
      }

      stream.close();
   }

   public JsonObject writeSettings(Feature feature) {
      JsonObject object = new JsonObject();
      JsonParser jp = new JsonParser();

      for(Setting setting : feature.getSettings()) {
         if (setting.isEnumSetting()) {
            EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
            object.add(setting.getName(), converter.doForward((Enum)setting.getValue()));
         } else {
            if (setting.isStringSetting()) {
               String str = (String)setting.getValue();
               setting.setValue(str.replace(" ", "_"));
            }

            try {
               object.add(setting.getName(), jp.parse(setting.getValueAsString()));
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         }
      }

      return object;
   }

   public String getDirectory(Feature feature) {
      String directory = "";
      if (feature instanceof Module) {
         directory = directory + ((Module)feature).getCategory().getName() + "/";
      }

      return directory;
   }
}
