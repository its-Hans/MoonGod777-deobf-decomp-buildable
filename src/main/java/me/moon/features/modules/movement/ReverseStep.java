//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.movement;

import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class ReverseStep extends Module {
   private final Setting<Boolean> twoBlocks = this.register(new Setting<>("2Blocks", false));

   public ReverseStep() {
      super("ReverseStep", "ReverseStep", Module.Category.MOVEMENT, true, false, false);
   }

   @Override
   public void onUpdate() {
      if (!fullNullCheck()) {
         IBlockState touchingState = mc.world
            .getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ).down(2));
         IBlockState touchingState2 = mc.world
            .getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ).down(3));
         if (!mc.player.isInLava() && !mc.player.isInWater()) {
            if (touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN) {
               if ((
                     this.twoBlocks.getValue() && touchingState2.getBlock() == Blocks.BEDROCK
                        || this.twoBlocks.getValue() && touchingState2.getBlock() == Blocks.OBSIDIAN
                  )
                  && mc.player.onGround) {
                  --mc.player.motionY;
               }
            } else if (mc.player.onGround) {
               --mc.player.motionY;
            }
         }
      }
   }
}
