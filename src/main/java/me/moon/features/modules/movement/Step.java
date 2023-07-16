//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.movement;

import me.moon.Moon;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.MotionUtil;
import net.minecraft.network.play.client.CPacketPlayer.Position;

public class Step extends Module {
   public Setting<Double> height = this.register(new Setting<>("Height", 2.5, 0.5, 2.5));

   public Step() {
      super("Step", "Step", Module.Category.MOVEMENT, true, false, false);
   }

   @Override
   public void onTick() {
      if (!fullNullCheck()) {
         if (!mc.player.isInWater()
            && !mc.player.isInLava()
            && !mc.player.isOnLadder()
            && !mc.gameSettings.keyBindJump.isKeyDown()) {
            if (!Moon.moduleManager.isModuleEnabled("Speed")) {
               double[] dir = MotionUtil.forward(0.1);
               boolean twofive = false;
               boolean two = false;
               boolean onefive = false;
               boolean one = false;
               if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.6, dir[1])).isEmpty()
                  && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.4, dir[1])).isEmpty()) {
                  twofive = true;
               }

               if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.1, dir[1])).isEmpty()
                  && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.9, dir[1])).isEmpty()) {
                  two = true;
               }

               if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.6, dir[1])).isEmpty()
                  && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.4, dir[1])).isEmpty()) {
                  onefive = true;
               }

               if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.0, dir[1])).isEmpty()
                  && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 0.6, dir[1])).isEmpty()) {
                  one = true;
               }

               if (mc.player.collidedHorizontally
                  && (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F)
                  && mc.player.onGround) {
                  if (one && this.height.getValue() >= 1.0) {
                     double[] oneOffset = new double[]{0.42, 0.753};

                     for(int i = 0; i < oneOffset.length; ++i) {
                        mc.player
                           .connection
                           .sendPacket(
                              new Position(
                                 mc.player.posX,
                                 mc.player.posY + oneOffset[i],
                                 mc.player.posZ,
                                 mc.player.onGround
                              )
                           );
                     }

                     mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ);
                  }

                  if (onefive && this.height.getValue() >= 1.5) {
                     double[] oneFiveOffset = new double[]{0.42, 0.75, 1.0, 1.16, 1.23, 1.2};

                     for(int i = 0; i < oneFiveOffset.length; ++i) {
                        mc.player
                           .connection
                           .sendPacket(
                              new Position(
                                 mc.player.posX,
                                 mc.player.posY + oneFiveOffset[i],
                                 mc.player.posZ,
                                 mc.player.onGround
                              )
                           );
                     }

                     mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5, mc.player.posZ);
                  }

                  if (two && this.height.getValue() >= 2.0) {
                     double[] twoOffset = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};

                     for(int i = 0; i < twoOffset.length; ++i) {
                        mc.player
                           .connection
                           .sendPacket(
                              new Position(
                                 mc.player.posX,
                                 mc.player.posY + twoOffset[i],
                                 mc.player.posZ,
                                 mc.player.onGround
                              )
                           );
                     }

                     mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);
                  }

                  if (twofive && this.height.getValue() >= 2.5) {
                     double[] twoFiveOffset = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};

                     for(int i = 0; i < twoFiveOffset.length; ++i) {
                        mc.player
                           .connection
                           .sendPacket(
                              new Position(
                                 mc.player.posX,
                                 mc.player.posY + twoFiveOffset[i],
                                 mc.player.posZ,
                                 mc.player.onGround
                              )
                           );
                     }

                     mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5, mc.player.posZ);
                  }
               }
            }
         }
      }
   }

   @Override
   public void onDisable() {
      mc.player.stepHeight = 0.5F;
   }
}
