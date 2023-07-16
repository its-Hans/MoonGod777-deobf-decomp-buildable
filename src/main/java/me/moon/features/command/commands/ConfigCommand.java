package me.moon.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.moon.Moon;
import me.moon.features.command.Command;

public class ConfigCommand extends Command {
   public ConfigCommand() {
      super("config", new String[]{"<save/load>"});
   }

   @Override
   public void execute(String[] commands) {
      if (commands.length == 1) {
         sendMessage(".config save <list> / .config load <list>");
      } else {
         if (commands.length == 2) {
            if ("list".equals(commands[0])) {
               String configs = "Configs: ";
               File file = new File("moon/");
               List<File> directories = Arrays.stream(file.listFiles())
                  .filter(File::isDirectory)
                  .filter(f -> !f.getName().equals("util"))
                  .collect(Collectors.toList());
               StringBuilder builder = new StringBuilder(configs);

               for(File file1 : directories) {
                  builder.append(file1.getName() + ", ");
               }

               configs = builder.toString();
               sendMessage(configs);
            } else {
               sendMessage(".config save <list> / .config load <list>");
            }
         }

         if (commands.length >= 3) {
            String var9 = commands[0];
            switch(var9) {
               case "save":
                  Moon.configManager.saveConfig(commands[1]);
                  sendMessage(ChatFormatting.GREEN + "Config '" + commands[1] + "' has been saved.");
                  return;
               case "load":
                  if (Moon.configManager.configExists(commands[1])) {
                     Moon.configManager.loadConfig(commands[1]);
                     sendMessage(ChatFormatting.GREEN + "Config '" + commands[1] + "' has been loaded.");
                  } else {
                     sendMessage(ChatFormatting.RED + "Config '" + commands[1] + "' does not exist.");
                  }

                  return;
               default:
                  sendMessage(".config save <list> / .config load <list>");
            }
         }
      }
   }
}
