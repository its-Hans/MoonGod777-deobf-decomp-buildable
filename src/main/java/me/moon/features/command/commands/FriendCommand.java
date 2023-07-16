package me.moon.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.moon.Moon;
import me.moon.features.command.Command;
import me.moon.manager.FriendManager;

public class FriendCommand extends Command {
   public FriendCommand() {
      super("friend", new String[]{"<add/del/name/clear>", "<name>"});
   }

   @Override
   public void execute(String[] commands) {
      if (commands.length != 1) {
         if (commands.length == 2) {
            String var8 = commands[0];
            byte var10 = -1;
            switch(var8.hashCode()) {
               case 108404047:
                  if (var8.equals("reset")) {
                     var10 = 0;
                  }
               default:
                  switch(var10) {
                     case 0:
                        Moon.friendManager.onLoad();
                        sendMessage("Friends got reset.");
                        return;
                     default:
                        sendMessage(commands[0] + (Moon.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
                  }
            }
         } else {
            if (commands.length >= 2) {
               String var7 = commands[0];
               switch(var7) {
                  case "add":
                     Moon.friendManager.addFriend(commands[1]);
                     sendMessage(ChatFormatting.GREEN + commands[1] + " has been friended");
                     return;
                  case "del":
                     Moon.friendManager.removeFriend(commands[1]);
                     sendMessage(ChatFormatting.RED + commands[1] + " has been unfriended");
                     return;
                  default:
                     sendMessage(".friend add <name> / .friend del <name>)");
               }
            }
         }
      } else {
         if (Moon.friendManager.getFriends().isEmpty()) {
            sendMessage(".friend add <name> / .friend del <name>");
         } else {
            String f = "Friends: ";

            for(FriendManager.Friend friend : Moon.friendManager.getFriends()) {
               try {
                  f = f + friend.getUsername() + ", ";
               } catch (Exception var6) {
               }
            }

            sendMessage(f);
         }
      }
   }
}
