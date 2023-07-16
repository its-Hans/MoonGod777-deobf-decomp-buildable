package me.moon.mixin;

import java.util.Map;
import me.moon.Moon;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

@Name("Moon")
public class MoonLoader implements IFMLLoadingPlugin {
   private static boolean isObfuscatedEnvironment = false;

   public MoonLoader() {
      Moon.LOGGER.info("\n\nLoading mixins by 404\n\n");
      MixinBootstrap.init();
      Mixins.addConfiguration("mixins.moon.json");
      MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
      Moon.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
   }

   public String[] getASMTransformerClass() {
      return new String[0];
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      isObfuscatedEnvironment = (Boolean) data.get("runtimeDeobfuscationEnabled");
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
