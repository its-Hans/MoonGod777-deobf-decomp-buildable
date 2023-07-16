package me.moon.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.moon.Moon;
import me.moon.features.command.Command;

public class HelpCommand extends Command {
   public HelpCommand() {
      super("help");
   }

   @Override
   public void execute(String[] commands) {
      sendMessage("Commands: ");

      for(Command command : Moon.commandManager.getCommands()) {
         sendMessage(ChatFormatting.GRAY + Moon.commandManager.getPrefix() + command.getName());
      }
   }
}
