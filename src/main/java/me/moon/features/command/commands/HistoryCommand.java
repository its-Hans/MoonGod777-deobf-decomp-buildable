package me.moon.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.List;
import java.util.UUID;
import me.moon.features.command.Command;
import me.moon.util.PlayerUtil;

public class HistoryCommand extends Command {
   public HistoryCommand() {
      super("history", new String[]{"<player>"});
   }

   @Override
   public void execute(String[] commands) {
      if (commands.length == 1 || commands.length == 0) {
         sendMessage(ChatFormatting.RED + "Please specify a player.");
      }

      UUID uuid;
      try {
         uuid = PlayerUtil.getUUIDFromName(commands[0]);
      } catch (Exception var7) {
         sendMessage("An error occured.");
         return;
      }

      List<String> names;
      try {
         names = PlayerUtil.getHistoryOfNames(uuid);
      } catch (Exception var6) {
         sendMessage("An error occured.");
         return;
      }

      if (names != null) {
         sendMessage(commands[0] + "Â´s name history:");

         for(String name : names) {
            sendMessage(name);
         }
      } else {
         sendMessage("No names found.");
      }
   }
}
