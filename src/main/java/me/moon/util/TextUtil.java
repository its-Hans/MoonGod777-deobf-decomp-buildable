package me.moon.util;

import com.mojang.realmsclient.gui.ChatFormatting;

public class TextUtil {
   public static String coloredString(String string, TextUtil.Color color) {
      String coloredString = string;
      switch(color) {
         case AQUA:
            coloredString = ChatFormatting.AQUA + string + ChatFormatting.RESET;
            break;
         case WHITE:
            coloredString = ChatFormatting.WHITE + string + ChatFormatting.RESET;
            break;
         case BLACK:
            coloredString = ChatFormatting.BLACK + string + ChatFormatting.RESET;
            break;
         case DARK_BLUE:
            coloredString = ChatFormatting.DARK_BLUE + string + ChatFormatting.RESET;
            break;
         case DARK_GREEN:
            coloredString = ChatFormatting.DARK_GREEN + string + ChatFormatting.RESET;
            break;
         case DARK_AQUA:
            coloredString = ChatFormatting.DARK_AQUA + string + ChatFormatting.RESET;
            break;
         case DARK_RED:
            coloredString = ChatFormatting.DARK_RED + string + ChatFormatting.RESET;
            break;
         case DARK_PURPLE:
            coloredString = ChatFormatting.DARK_PURPLE + string + ChatFormatting.RESET;
            break;
         case GOLD:
            coloredString = ChatFormatting.GOLD + string + ChatFormatting.RESET;
            break;
         case DARK_GRAY:
            coloredString = ChatFormatting.DARK_GRAY + string + ChatFormatting.RESET;
            break;
         case GRAY:
            coloredString = ChatFormatting.GRAY + string + ChatFormatting.RESET;
            break;
         case BLUE:
            coloredString = ChatFormatting.BLUE + string + ChatFormatting.RESET;
            break;
         case RED:
            coloredString = ChatFormatting.RED + string + ChatFormatting.RESET;
            break;
         case GREEN:
            coloredString = ChatFormatting.GREEN + string + ChatFormatting.RESET;
            break;
         case LIGHT_PURPLE:
            coloredString = ChatFormatting.LIGHT_PURPLE + string + ChatFormatting.RESET;
            break;
         case YELLOW:
            coloredString = ChatFormatting.YELLOW + string + ChatFormatting.RESET;
      }

      return coloredString;
   }

   public static enum Color {
      NONE,
      WHITE,
      BLACK,
      DARK_BLUE,
      DARK_GREEN,
      DARK_AQUA,
      DARK_RED,
      DARK_PURPLE,
      GOLD,
      GRAY,
      DARK_GRAY,
      BLUE,
      GREEN,
      AQUA,
      RED,
      LIGHT_PURPLE,
      YELLOW;
   }
}
