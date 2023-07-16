//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.render;

import java.awt.Color;
import me.moon.event.events.Render3DEvent;
import me.moon.features.modules.Module;
import me.moon.features.modules.client.ClickGui;
import me.moon.features.setting.Setting;
import me.moon.util.ColorUtil;
import me.moon.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

public class BlockHighlight extends Module {
   private final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0F, 0.1F, 5.0F));
   private final Setting<Integer> cAlpha = this.register(new Setting<>("Alpha", 255, 0, 255));

   public BlockHighlight() {
      super("BlockHighlight", "Highlights the block u look at.", Module.Category.RENDER, false, false, false);
   }

   @Override
   public void onRender3D(Render3DEvent event) {
      RayTraceResult ray = mc.objectMouseOver;
      if (ray != null && ray.typeOfHit == Type.BLOCK) {
         BlockPos blockpos = ray.getBlockPos();
         RenderUtil.drawBlockOutline(
            blockpos,
            ClickGui.getInstance().rainbow.getValue()
               ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue())
               : new Color(
                  ClickGui.getInstance().red.getValue(),
                  ClickGui.getInstance().green.getValue(),
                  ClickGui.getInstance().blue.getValue(),
                  this.cAlpha.getValue()
               ),
            this.lineWidth.getValue(),
            false
         );
      }
   }
}
