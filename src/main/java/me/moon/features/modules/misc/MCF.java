//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.moon.Moon;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import org.lwjgl.input.Mouse;

public class MCF extends Module {
   private boolean clicked = false;

   public MCF() {
      super("MCF", "Middleclick Friends.", Module.Category.MISC, true, false, false);
   }

   @Override
   public void onUpdate() {
      if (Mouse.isButtonDown(2)) {
         if (!this.clicked && mc.currentScreen == null) {
            this.onClick();
         }

         this.clicked = true;
      } else {
         this.clicked = false;
      }
   }

   private void onClick() {
      RayTraceResult result = mc.objectMouseOver;
      if (result != null && result.typeOfHit == Type.ENTITY) {
         Entity entity = result.entityHit;
         if (result.entityHit instanceof EntityPlayer) {
            if (Moon.friendManager.isFriend(entity.getName())) {
               Moon.friendManager.removeFriend(entity.getName());
               Command.sendMessage(ChatFormatting.RED + entity.getName() + ChatFormatting.RED + " has been unfriended.");
            } else {
               Moon.friendManager.addFriend(entity.getName());
               Command.sendMessage(ChatFormatting.AQUA + entity.getName() + ChatFormatting.AQUA + " has been friended.");
            }
         }
      }

      this.clicked = true;
   }
}
