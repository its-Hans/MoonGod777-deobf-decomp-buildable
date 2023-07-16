package me.moon.features.command.commands;

import me.moon.Moon;
import me.moon.features.command.Command;

public class UnloadCommand extends Command {
   public UnloadCommand() {
      super("unload", new String[0]);
   }

   @Override
   public void execute(String[] commands) {
      Moon.unload(true);
   }
}
