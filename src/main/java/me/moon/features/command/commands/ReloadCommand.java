package me.moon.features.command.commands;

import me.moon.Moon;
import me.moon.features.command.Command;

public class ReloadCommand extends Command {
   public ReloadCommand() {
      super("reload", new String[0]);
   }

   @Override
   public void execute(String[] commands) {
      Moon.reload();
   }
}
