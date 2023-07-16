package me.moon.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.LinkedList;
import me.moon.features.Feature;
import me.moon.features.command.Command;
import me.moon.features.command.commands.BindCommand;
import me.moon.features.command.commands.ConfigCommand;
import me.moon.features.command.commands.FriendCommand;
import me.moon.features.command.commands.HelpCommand;
import me.moon.features.command.commands.HistoryCommand;
import me.moon.features.command.commands.ModuleCommand;
import me.moon.features.command.commands.PrefixCommand;
import me.moon.features.command.commands.ReloadCommand;
import me.moon.features.command.commands.ReloadSoundCommand;
import me.moon.features.command.commands.UnloadCommand;

public class CommandManager extends Feature {
   private final ArrayList<Command> commands = new ArrayList<>();
   private String clientMessage = "[MoonGod]";
   private String prefix = ".";

   public CommandManager() {
      super("Command");
      this.commands.add(new BindCommand());
      this.commands.add(new ModuleCommand());
      this.commands.add(new PrefixCommand());
      this.commands.add(new ConfigCommand());
      this.commands.add(new FriendCommand());
      this.commands.add(new HelpCommand());
      this.commands.add(new ReloadCommand());
      this.commands.add(new UnloadCommand());
      this.commands.add(new ReloadSoundCommand());
      this.commands.add(new HistoryCommand());
   }

   public static String[] removeElement(String[] input, int indexToDelete) {
      LinkedList<String> result = new LinkedList<>();

      for(int i = 0; i < input.length; ++i) {
         if (i != indexToDelete) {
            result.add(input[i]);
         }
      }

      return result.toArray(input);
   }

   private static String strip(String str, String key) {
      return str.startsWith(key) && str.endsWith(key) ? str.substring(key.length(), str.length() - key.length()) : str;
   }

   public void executeCommand(String command) {
      String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
      String name = parts[0].substring(1);
      String[] args = removeElement(parts, 0);

      for(int i = 0; i < args.length; ++i) {
         if (args[i] != null) {
            args[i] = strip(args[i], "\"");
         }
      }

      for(Command c : this.commands) {
         if (c.getName().equalsIgnoreCase(name)) {
            c.execute(parts);
            return;
         }
      }

      Command.sendMessage(ChatFormatting.GRAY + "Command not found, type 'help' for the commands list.");
   }

   public Command getCommandByName(String name) {
      for(Command command : this.commands) {
         if (command.getName().equals(name)) {
            return command;
         }
      }

      return null;
   }

   public ArrayList<Command> getCommands() {
      return this.commands;
   }

   public String getClientMessage() {
      return this.clientMessage;
   }

   public void setClientMessage(String clientMessage) {
      this.clientMessage = clientMessage;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }
}
