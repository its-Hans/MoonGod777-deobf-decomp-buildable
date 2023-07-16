//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.moon.Moon;
import me.moon.features.Feature;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

public abstract class Command extends Feature {
   protected String name;
   protected String[] commands;

   public Command(String name) {
      super(name);
      this.name = name;
      this.commands = new String[]{""};
   }

   public Command(String name, String[] commands) {
      super(name);
      this.name = name;
      this.commands = commands;
   }

   public static void sendMessage(String message) {
      sendSilentMessage(Moon.commandManager.getClientMessage() + " " + ChatFormatting.GRAY + message);
   }

   public static void sendSilentMessage(String message) {
      if (!nullCheck()) {
         mc.player.sendMessage(new Command.ChatMessage(message));
      }
   }

   public static String getCommandPrefix() {
      return Moon.commandManager.getPrefix();
   }

   public abstract void execute(String[] var1);

   @Override
   public String getName() {
      return this.name;
   }

   public String[] getCommands() {
      return this.commands;
   }

   public static class ChatMessage extends TextComponentBase {
      private final String text;

      public ChatMessage(String text) {
         Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
         Matcher matcher = pattern.matcher(text);
         StringBuffer stringBuffer = new StringBuffer();

         while(matcher.find()) {
            String replacement = matcher.group().substring(1);
            matcher.appendReplacement(stringBuffer, replacement);
         }

         matcher.appendTail(stringBuffer);
         this.text = stringBuffer.toString();
      }

      public String getUnformattedComponentText() {
         return this.text;
      }

      public ITextComponent createCopy() {
         return null;
      }

      public ITextComponent shallowCopy() {
         return new Command.ChatMessage(this.text);
      }
   }
}
