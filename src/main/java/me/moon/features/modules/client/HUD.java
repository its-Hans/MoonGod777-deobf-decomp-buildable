//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.moon.Moon;
import me.moon.event.events.ClientEvent;
import me.moon.event.events.Render2DEvent;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.ColorUtil;
import me.moon.util.EntityUtil;
import me.moon.util.MathUtil;
import me.moon.util.RenderUtil;
import me.moon.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
   private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
   private static HUD INSTANCE = new HUD();
   private final Setting<Boolean> renderingUp = this.register(new Setting<>("RenderingUp", false));
   private final Setting<Boolean> waterMark = this.register(new Setting<>("Watermark", false));
   private final Setting<Boolean> arrayList = this.register(new Setting<>("ActiveModules", false));
   private final Setting<Boolean> coords = this.register(new Setting<>("Coords", false));
   private final Setting<Boolean> direction = this.register(new Setting<>("Direction", false));
   private final Setting<Boolean> armor = this.register(new Setting<>("Armor", false));
   private final Setting<Boolean> totems = this.register(new Setting<>("Totems", false));
   private final Setting<Boolean> greeter = this.register(new Setting<>("Welcomer", false));
   private final Setting<Boolean> speed = this.register(new Setting<>("Speed", false));
   private final Setting<Boolean> potions = this.register(new Setting<>("Potions", false));
   private final Setting<Boolean> ping = this.register(new Setting<>("Ping", false));
   private final Setting<Boolean> tps = this.register(new Setting<>("TPS", false));
   private final Setting<Boolean> fps = this.register(new Setting<>("FPS", false));
   private final Setting<Boolean> lag = this.register(new Setting<>("LagNotifier", false));
   private final Setting<Boolean> hitMarkers = this.register(new Setting<>("HitMarkers", true));
   private final Map<String, Integer> players = new HashMap<>();
   public Setting<TextUtil.Color> bracketColor = this.register(new Setting<>("BracketColor", TextUtil.Color.WHITE));
   public Setting<TextUtil.Color> commandColor = this.register(new Setting<>("NameColor", TextUtil.Color.WHITE));
   public Setting<String> commandBracket = this.register(new Setting<>("Bracket", "<"));
   public Setting<String> commandBracket2 = this.register(new Setting<>("Bracket2", ">"));
   public Setting<Boolean> notifyToggles = this.register(new Setting<>("ChatNotify", false));
   public Setting<Integer> animationHorizontalTime = this.register(new Setting<>("AnimationHTime", 500, 1, 1000, v -> this.arrayList.getValue()));
   public Setting<Integer> animationVerticalTime = this.register(new Setting<>("AnimationVTime", 50, 1, 500, v -> this.arrayList.getValue()));
   public Setting<HUD.RenderingMode> renderingMode = this.register(new Setting<>("Ordering", HUD.RenderingMode.ABC));
   public Setting<Integer> waterMarkX = this.register(new Setting<>("WatermarkPosX", 2, 0, 100, v -> this.waterMark.getValue()));
   public Setting<Integer> waterMarkY = this.register(new Setting<>("WatermarkPosY", 2, 0, 100, v -> this.waterMark.getValue()));
   public Setting<Boolean> time = this.register(new Setting<>("Time", false, "The time"));
   public Setting<Integer> lagTime = this.register(new Setting<>("LagTime", 1000, 0, 2000));
   private int color;
   private boolean shouldIncrement;
   private int hitMarkerTimer;

   public HUD() {
      super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
      this.setInstance();
   }

   public static HUD getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new HUD();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @Override
   public void onUpdate() {
      if (this.shouldIncrement) {
         ++this.hitMarkerTimer;
      }

      if (this.hitMarkerTimer == 10) {
         this.hitMarkerTimer = 0;
         this.shouldIncrement = false;
      }
   }

   @Override
   public void onRender2D(Render2DEvent event) {
      if (!fullNullCheck()) {
         int width = this.renderer.scaledWidth;
         int height = this.renderer.scaledHeight;
         this.color = ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
         if (this.waterMark.getValue()) {
            String string = "MoonGod | 777";
            if (ClickGui.getInstance().rainbow.getValue()) {
               if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                  this.renderer
                     .drawString(
                        string,
                        (float)this.waterMarkX.getValue().intValue(),
                        (float)this.waterMarkY.getValue().intValue(),
                        ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(),
                        true
                     );
               } else {
                  int[] var5 = new int[]{1};
                  char[] stringToCharArray = string.toCharArray();
                  float var7 = 0.0F;

                  for(int c : stringToCharArray) {
                     this.renderer
                        .drawString(
                           String.valueOf(c),
                           (float)this.waterMarkX.getValue().intValue() + var7,
                           (float)this.waterMarkY.getValue().intValue(),
                           ColorUtil.rainbow(var5[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(),
                           true
                        );
                     var7 += (float)this.renderer.getStringWidth(String.valueOf(c));
                     var5[0]++;
                  }
               }
            } else {
               this.renderer.drawString(string, (float)this.waterMarkX.getValue().intValue(), (float)this.waterMarkY.getValue().intValue(), this.color, true);
            }
         }

         int[] var46 = new int[]{1};
         int var47 = mc.currentScreen instanceof GuiChat && !this.renderingUp.getValue() ? 14 : 0;
         if (this.arrayList.getValue()) {
            if (this.renderingUp.getValue()) {
               if (this.renderingMode.getValue() == HUD.RenderingMode.ABC) {
                  for(int var6 = 0; var6 < Moon.moduleManager.sortedModulesABC.size(); ++var6) {
                     String var58 = Moon.moduleManager.sortedModulesABC.get(var6);
                     this.renderer
                        .drawString(
                           var58,
                           (float)(width - 2 - this.renderer.getStringWidth(var58)),
                           (float)(2 + var47 * 10),
                           ClickGui.getInstance().rainbow.getValue()
                              ? (
                                 ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                    ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                    : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                              )
                              : this.color,
                           true
                        );
                     ++var47;
                     var46[0]++;
                  }
               } else {
                  for(int var48 = 0; var48 < Moon.moduleManager.sortedModules.size(); ++var48) {
                     Module var59 = Moon.moduleManager.sortedModules.get(var48);
                     String str = var59.getDisplayName()
                        + ChatFormatting.GRAY
                        + (var59.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + var59.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                     this.renderer
                        .drawString(
                           str,
                           (float)(width - 2 - this.renderer.getStringWidth(str)),
                           (float)(2 + var47 * 10),
                           ClickGui.getInstance().rainbow.getValue()
                              ? (
                                 ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                    ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                    : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                              )
                              : this.color,
                           true
                        );
                     ++var47;
                     var46[0]++;
                  }
               }
            } else if (this.renderingMode.getValue() == HUD.RenderingMode.ABC) {
               for(int var49 = 0; var49 < Moon.moduleManager.sortedModulesABC.size(); ++var49) {
                  String var60 = Moon.moduleManager.sortedModulesABC.get(var49);
                  var47 += 10;
                  this.renderer
                     .drawString(
                        var60,
                        (float)(width - 2 - this.renderer.getStringWidth(var60)),
                        (float)(height - var47),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }
            } else {
               for(int var50 = 0; var50 < Moon.moduleManager.sortedModules.size(); ++var50) {
                  Module var61 = Moon.moduleManager.sortedModules.get(var50);
                  String var88 = var61.getDisplayName()
                     + ChatFormatting.GRAY
                     + (var61.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + var61.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                  var47 += 10;
                  this.renderer
                     .drawString(
                        var88,
                        (float)(width - 2 - this.renderer.getStringWidth(var88)),
                        (float)(height - var47),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }
            }
         }

         int var51 = mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() ? 13 : (this.renderingUp.getValue() ? -2 : 0);
         if (this.renderingUp.getValue()) {
            if (this.potions.getValue()) {
               for(PotionEffect var78 : new ArrayList<>(Minecraft.getMinecraft().player.getActivePotionEffects())) {
                  String var89 = Moon.potionManager.getColoredPotionString(var78);
                  var51 += 10;
                  this.renderer
                     .drawString(
                        var89,
                        (float)(width - this.renderer.getStringWidth(var89) - 2),
                        (float)(height - 2 - var51),
                        var78.getPotion().getLiquidColor(),
                        true
                     );
               }
            }

            if (this.speed.getValue()) {
               String var63 = ChatFormatting.GRAY + "Speed " + ChatFormatting.WHITE + Moon.speedManager.getSpeedKpH() + " km/h";
               var51 += 10;
               this.renderer
                  .drawString(
                     var63,
                     (float)(width - this.renderer.getStringWidth(var63) - 2),
                     (float)(height - 2 - var51),
                     ClickGui.getInstance().rainbow.getValue()
                        ? (
                           ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                              ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                              : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                        )
                        : this.color,
                     true
                  );
               var46[0]++;
            }

            if (this.time.getValue()) {
               String var64 = ChatFormatting.GRAY + "Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
               var51 += 10;
               this.renderer
                  .drawString(
                     var64,
                     (float)(width - this.renderer.getStringWidth(var64) - 2),
                     (float)(height - 2 - var51),
                     ClickGui.getInstance().rainbow.getValue()
                        ? (
                           ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                              ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                              : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                        )
                        : this.color,
                     true
                  );
               var46[0]++;
            }

            if (this.tps.getValue()) {
               String var65 = ChatFormatting.GRAY + "TPS " + ChatFormatting.WHITE + Moon.serverManager.getTPS();
               var51 += 10;
               this.renderer
                  .drawString(
                     var65,
                     (float)(width - this.renderer.getStringWidth(var65) - 2),
                     (float)(height - 2 - var51),
                     ClickGui.getInstance().rainbow.getValue()
                        ? (
                           ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                              ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                              : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                        )
                        : this.color,
                     true
                  );
               var46[0]++;
            }

            String var66 = ChatFormatting.GRAY + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            String var74 = ChatFormatting.GRAY + "Ping " + ChatFormatting.WHITE + Moon.serverManager.getPing();
            if (this.renderer.getStringWidth(var74) > this.renderer.getStringWidth(var66)) {
               if (this.ping.getValue()) {
                  var51 += 10;
                  this.renderer
                     .drawString(
                        var74,
                        (float)(width - this.renderer.getStringWidth(var74) - 2),
                        (float)(height - 2 - var51),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }

               if (this.fps.getValue()) {
                  var51 += 10;
                  this.renderer
                     .drawString(
                        var66,
                        (float)(width - this.renderer.getStringWidth(var66) - 2),
                        (float)(height - 2 - var51),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }
            } else {
               if (this.fps.getValue()) {
                  var51 += 10;
                  this.renderer
                     .drawString(
                        var66,
                        (float)(width - this.renderer.getStringWidth(var66) - 2),
                        (float)(height - 2 - var51),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }

               if (this.ping.getValue()) {
                  var51 += 10;
                  this.renderer
                     .drawString(
                        var74,
                        (float)(width - this.renderer.getStringWidth(var74) - 2),
                        (float)(height - 2 - var51),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }
            }
         } else {
            if (this.potions.getValue()) {
               for(PotionEffect var79 : new ArrayList<>(Minecraft.getMinecraft().player.getActivePotionEffects())) {
                  String var90 = Moon.potionManager.getColoredPotionString(var79);
                  this.renderer
                     .drawString(
                        var90, (float)(width - this.renderer.getStringWidth(var90) - 2), (float)(2 + var51++ * 10), var79.getPotion().getLiquidColor(), true
                     );
               }
            }

            if (this.speed.getValue()) {
               String var68 = ChatFormatting.GRAY + "Speed " + ChatFormatting.WHITE + Moon.speedManager.getSpeedKpH() + " km/h";
               this.renderer
                  .drawString(
                     var68,
                     (float)(width - this.renderer.getStringWidth(var68) - 2),
                     (float)(2 + var51++ * 10),
                     ClickGui.getInstance().rainbow.getValue()
                        ? (
                           ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                              ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                              : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                        )
                        : this.color,
                     true
                  );
               var46[0]++;
            }

            if (this.time.getValue()) {
               String var69 = ChatFormatting.GRAY + "Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
               this.renderer
                  .drawString(
                     var69,
                     (float)(width - this.renderer.getStringWidth(var69) - 2),
                     (float)(2 + var51++ * 10),
                     ClickGui.getInstance().rainbow.getValue()
                        ? (
                           ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                              ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                              : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                        )
                        : this.color,
                     true
                  );
               var46[0]++;
            }

            if (this.tps.getValue()) {
               String var70 = ChatFormatting.GRAY + "TPS " + ChatFormatting.WHITE + Moon.serverManager.getTPS();
               this.renderer
                  .drawString(
                     var70,
                     (float)(width - this.renderer.getStringWidth(var70) - 2),
                     (float)(2 + var51++ * 10),
                     ClickGui.getInstance().rainbow.getValue()
                        ? (
                           ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                              ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                              : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                        )
                        : this.color,
                     true
                  );
               var46[0]++;
            }

            String var71 = ChatFormatting.GRAY + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            String var76 = ChatFormatting.GRAY + "Ping " + ChatFormatting.WHITE + Moon.serverManager.getPing();
            if (this.renderer.getStringWidth(var76) > this.renderer.getStringWidth(var71)) {
               if (this.ping.getValue()) {
                  this.renderer
                     .drawString(
                        var76,
                        (float)(width - this.renderer.getStringWidth(var76) - 2),
                        (float)(2 + var51++ * 10),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }

               if (this.fps.getValue()) {
                  this.renderer
                     .drawString(
                        var71,
                        (float)(width - this.renderer.getStringWidth(var71) - 2),
                        (float)(2 + var51++ * 10),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }
            } else {
               if (this.fps.getValue()) {
                  this.renderer
                     .drawString(
                        var71,
                        (float)(width - this.renderer.getStringWidth(var71) - 2),
                        (float)(2 + var51++ * 10),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }

               if (this.ping.getValue()) {
                  this.renderer
                     .drawString(
                        var76,
                        (float)(width - this.renderer.getStringWidth(var76) - 2),
                        (float)(2 + var51++ * 10),
                        ClickGui.getInstance().rainbow.getValue()
                           ? (
                              ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up
                                 ? ColorUtil.rainbow(var46[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                                 : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()
                           )
                           : this.color,
                        true
                     );
                  var46[0]++;
               }
            }
         }

         boolean var72 = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
         int var77 = (int)mc.player.posX;
         int var80 = (int)mc.player.posY;
         int var81 = (int)mc.player.posZ;
         float var11 = !var72 ? 0.125F : 8.0F;
         int hposX = (int)(mc.player.posX * (double)var11);
         int hposZ = (int)(mc.player.posZ * (double)var11);
         var51 = mc.currentScreen instanceof GuiChat ? 14 : 0;
         String coordinates = ChatFormatting.WHITE
            + "XYZ "
            + ChatFormatting.RESET
            + (
               var72
                  ? var77
                     + ", "
                     + var80
                     + ", "
                     + var81
                     + ChatFormatting.WHITE
                     + " ["
                     + ChatFormatting.RESET
                     + hposX
                     + ", "
                     + hposZ
                     + ChatFormatting.WHITE
                     + "]"
                     + ChatFormatting.RESET
                  : var77
                     + ", "
                     + var80
                     + ", "
                     + var81
                     + ChatFormatting.WHITE
                     + " ["
                     + ChatFormatting.RESET
                     + hposX
                     + ", "
                     + hposZ
                     + ChatFormatting.WHITE
                     + "]"
            );
         String direction = this.direction.getValue() ? Moon.rotationManager.getDirection4D(false) : "";
         String coords = this.coords.getValue() ? coordinates : "";
         var51 += 10;
         if (ClickGui.getInstance().rainbow.getValue()) {
            String rainbowCoords = this.coords.getValue()
               ? "XYZ "
                  + (
                     var72
                        ? var77 + ", " + var80 + ", " + var81 + " [" + hposX + ", " + hposZ + "]"
                        : var77 + ", " + var80 + ", " + var81 + " [" + hposX + ", " + hposZ + "]"
                  )
               : "";
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
               this.renderer
                  .drawString(direction, 2.0F, (float)(height - var51 - 11), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
               this.renderer
                  .drawString(rainbowCoords, 2.0F, (float)(height - var51), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
               int[] counter2 = new int[]{1};
               char[] var82 = direction.toCharArray();
               float s = 0.0F;

               for(int var86 : var82) {
                  this.renderer
                     .drawString(
                        String.valueOf(var86),
                        2.0F + s,
                        (float)(height - var51 - 11),
                        ColorUtil.rainbow(counter2[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(),
                        true
                     );
                  s += (float)this.renderer.getStringWidth(String.valueOf(var86));
                  counter2[0]++;
               }

               int[] var83 = new int[]{1};
               char[] var84 = rainbowCoords.toCharArray();
               float var85 = 0.0F;

               for(int var87 : var84) {
                  this.renderer
                     .drawString(
                        String.valueOf(var87),
                        2.0F + var85,
                        (float)(height - var51),
                        ColorUtil.rainbow(var83[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(),
                        true
                     );
                  var85 += (float)this.renderer.getStringWidth(String.valueOf(var87));
                  var83[0]++;
               }
            }
         } else {
            this.renderer.drawString(direction, 2.0F, (float)(height - var51 - 11), this.color, true);
            this.renderer.drawString(coords, 2.0F, (float)(height - var51), this.color, true);
         }

         if (this.armor.getValue()) {
            this.renderArmorHUD();
         }

         if (this.totems.getValue()) {
            this.renderTotemHUD();
         }

         if (this.greeter.getValue()) {
            this.renderGreeter();
         }

         if (this.lag.getValue()) {
            this.renderLag();
         }

         if (this.hitMarkers.getValue() && this.hitMarkerTimer > 0) {
            this.drawHitMarkers();
         }
      }
   }

   public Map<String, Integer> getTextRadarPlayers() {
      return EntityUtil.getTextRadarPlayers();
   }

   public void renderGreeter() {
      int width = this.renderer.scaledWidth;
      String text = "";
      if (this.greeter.getValue()) {
         text = text + MathUtil.getTimeOfDay() + mc.player.getDisplayNameString();
      }

      if (ClickGui.getInstance().rainbow.getValue()) {
         if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
            this.renderer
               .drawString(
                  text,
                  (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F,
                  2.0F,
                  ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(),
                  true
               );
         } else {
            int[] counter1 = new int[]{1};
            char[] stringToCharArray = text.toCharArray();
            float i = 0.0F;

            for(char c : stringToCharArray) {
               this.renderer
                  .drawString(
                     String.valueOf(c),
                     (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F + i,
                     2.0F,
                     ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(),
                     true
                  );
               i += (float)this.renderer.getStringWidth(String.valueOf(c));
               counter1[0]++;
            }
         }
      } else {
         this.renderer.drawString(text, (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, this.color, true);
      }
   }

   public void renderLag() {
      int width = this.renderer.scaledWidth;
      if (Moon.serverManager.isServerNotResponding()) {
         String text = ChatFormatting.RED + "Server not responding " + MathUtil.round((float)Moon.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";
         this.renderer.drawString(text, (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, this.color, true);
      }
   }

   public void renderTotemHUD() {
      int width = this.renderer.scaledWidth;
      int height = this.renderer.scaledHeight;
      int totems = mc.player
         .inventory
         .mainInventory
         .stream()
         .filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING)
         .mapToInt(ItemStack::getCount)
         .sum();
      if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
         totems += mc.player.getHeldItemOffhand().getCount();
      }

      if (totems > 0) {
         GlStateManager.enableTexture2D();
         int i = width / 2;
         int y = height - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
         int x = i - 189 + 180 + 2;
         GlStateManager.enableDepth();
         RenderUtil.itemRender.zLevel = 200.0F;
         RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
         RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, totem, x, y, "");
         RenderUtil.itemRender.zLevel = 0.0F;
         GlStateManager.enableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         this.renderer.drawStringWithShadow(totems + "", (float)(x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float)(y + 9), 16777215);
         GlStateManager.enableDepth();
         GlStateManager.disableLighting();
      }
   }

   public void renderArmorHUD() {
      int width = this.renderer.scaledWidth;
      int height = this.renderer.scaledHeight;
      GlStateManager.enableTexture2D();
      int i = width / 2;
      int iteration = 0;
      int y = height - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);

      for(ItemStack is : mc.player.inventory.armorInventory) {
         ++iteration;
         if (!is.isEmpty()) {
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            this.renderer.drawStringWithShadow(s, (float)(x + 19 - 2 - this.renderer.getStringWidth(s)), (float)(y + 9), 16777215);
            int dmg = 0;
            float green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
            float red = 1.0F - green;
            dmg = 100 - (int)(red * 100.0F);
            this.renderer
               .drawStringWithShadow(
                  dmg + "",
                  (float)(x + 8 - this.renderer.getStringWidth(dmg + "") / 2),
                  (float)(y - 11),
                  ColorUtil.toRGBA((int)(red * 255.0F), (int)(green * 255.0F), 0)
               );
         }
      }

      GlStateManager.enableDepth();
      GlStateManager.disableLighting();
   }

   public void drawHitMarkers() {
      ScaledResolution resolution = new ScaledResolution(mc);
      RenderUtil.drawLine(
         (float)resolution.getScaledWidth() / 2.0F - 4.0F,
         (float)resolution.getScaledHeight() / 2.0F - 4.0F,
         (float)resolution.getScaledWidth() / 2.0F - 8.0F,
         (float)resolution.getScaledHeight() / 2.0F - 8.0F,
         1.0F,
         ColorUtil.toRGBA(255, 255, 255, 255)
      );
      RenderUtil.drawLine(
         (float)resolution.getScaledWidth() / 2.0F + 4.0F,
         (float)resolution.getScaledHeight() / 2.0F - 4.0F,
         (float)resolution.getScaledWidth() / 2.0F + 8.0F,
         (float)resolution.getScaledHeight() / 2.0F - 8.0F,
         1.0F,
         ColorUtil.toRGBA(255, 255, 255, 255)
      );
      RenderUtil.drawLine(
         (float)resolution.getScaledWidth() / 2.0F - 4.0F,
         (float)resolution.getScaledHeight() / 2.0F + 4.0F,
         (float)resolution.getScaledWidth() / 2.0F - 8.0F,
         (float)resolution.getScaledHeight() / 2.0F + 8.0F,
         1.0F,
         ColorUtil.toRGBA(255, 255, 255, 255)
      );
      RenderUtil.drawLine(
         (float)resolution.getScaledWidth() / 2.0F + 4.0F,
         (float)resolution.getScaledHeight() / 2.0F + 4.0F,
         (float)resolution.getScaledWidth() / 2.0F + 8.0F,
         (float)resolution.getScaledHeight() / 2.0F + 8.0F,
         1.0F,
         ColorUtil.toRGBA(255, 255, 255, 255)
      );
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(AttackEntityEvent event) {
      this.shouldIncrement = true;
   }

   @Override
   public void onLoad() {
      Moon.commandManager.setClientMessage(this.getCommandMessage());
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2 && this.equals(event.getSetting().getFeature())) {
         Moon.commandManager.setClientMessage(this.getCommandMessage());
      }
   }

   public String getCommandMessage() {
      return TextUtil.coloredString(this.commandBracket.getPlannedValue(), this.bracketColor.getPlannedValue())
         + TextUtil.coloredString("MoonGod", this.commandColor.getPlannedValue())
         + TextUtil.coloredString(this.commandBracket2.getPlannedValue(), this.bracketColor.getPlannedValue());
   }

   public void drawTextRadar(int yOffset) {
      if (!this.players.isEmpty()) {
         int y = this.renderer.getFontHeight() + 7 + yOffset;

         for(Entry<String, Integer> player : this.players.entrySet()) {
            String text = (String)player.getKey() + " ";
            int textheight = this.renderer.getFontHeight() + 1;
            this.renderer.drawString(text, 2.0F, (float)y, this.color, true);
            y += textheight;
         }
      }
   }

   public static enum RenderingMode {
      Length,
      ABC;
   }

   public static enum Sound {
      NONE,
      COD,
      CSGO;
   }
}
