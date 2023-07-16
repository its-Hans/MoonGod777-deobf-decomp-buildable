//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.movement;

import me.moon.event.events.PacketEvent;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import me.moon.util.EntityUtil;
import me.moon.util.InventoryUtil;
import me.moon.util.Timer;
import me.moon.util.Util;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFall extends Module {
   private static NoFall INSTANCE = new NoFall();
   private Setting<NoFall.Mode> mode = this.register(new Setting<>("Mode", NoFall.Mode.PACKET));
   private Setting<Integer> distance = this.register(
      new Setting<>("Distance", 15, 0, 50, v -> this.mode.getValue() == NoFall.Mode.BUCKET || this.mode.getValue() == NoFall.Mode.PACKET)
   );
   private Setting<Boolean> glide = this.register(new Setting<>("Glide", false, v -> this.mode.getValue() == NoFall.Mode.ELYTRA));
   private Setting<Boolean> silent = this.register(new Setting<>("Silent", true, v -> this.mode.getValue() == NoFall.Mode.ELYTRA));
   private Setting<Boolean> bypass = this.register(new Setting<>("Bypass", false, v -> this.mode.getValue() == NoFall.Mode.ELYTRA));
   private Timer timer = new Timer();
   private boolean equipped = false;
   private boolean gotElytra = false;
   private NoFall.State currentState = NoFall.State.FALL_CHECK;
   private static Timer bypassTimer = new Timer();
   private static int ogslot = -1;

   public NoFall() {
      super("NoFall", "Prevents fall damage.", Module.Category.MOVEMENT, true, false, false);
      this.setInstance();
   }

   public static NoFall getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new NoFall();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @Override
   public void onEnable() {
      ogslot = -1;
      this.currentState = NoFall.State.FALL_CHECK;
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck()) {
         if (this.mode.getValue() == NoFall.Mode.ELYTRA) {
            if (this.bypass.getValue()) {
               this.currentState = this.currentState.onSend(event);
            } else if (!this.equipped && event.getPacket() instanceof CPacketPlayer && mc.player.fallDistance >= 3.0F) {
               RayTraceResult result = null;
               if (!this.glide.getValue()) {
                  result = mc.world
                     .rayTraceBlocks(mc.player.getPositionVector(), mc.player.getPositionVector().add(0.0, -3.0, 0.0), true, true, false);
               }

               if (this.glide.getValue() || result != null && result.typeOfHit == Type.BLOCK) {
                  if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
                     mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_FALL_FLYING));
                  } else if (this.silent.getValue()) {
                     int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);
                     if (slot != -1) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, mc.player);
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_FALL_FLYING));
                     }

                     ogslot = slot;
                     this.equipped = true;
                  }
               }
            }
         }

         if (this.mode.getValue() == NoFall.Mode.PACKET && event.getPacket() instanceof CPacketPlayer) {
            if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
               return;
            }

            if (mc.player.fallDistance >= (float)this.distance.getValue().intValue()) {
               CPacketPlayer packet = event.getPacket();
               packet.onGround = true;
            }
         }
      }
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (!fullNullCheck()) {
         if ((this.equipped || this.bypass.getValue())
            && this.mode.getValue() == NoFall.Mode.ELYTRA
            && (event.getPacket() instanceof SPacketWindowItems || event.getPacket() instanceof SPacketSetSlot)) {
            if (this.bypass.getValue()) {
               this.currentState = this.currentState.onReceive(event);
            } else {
               this.gotElytra = true;
            }
         }
      }
   }

   @Override
   public void onUpdate() {
      if (!fullNullCheck()) {
         if (this.mode.getValue() == NoFall.Mode.ELYTRA) {
            if (this.bypass.getValue()) {
               this.currentState = this.currentState.onUpdate();
            } else if (this.silent.getValue() && this.equipped && this.gotElytra) {
               mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, ogslot, ClickType.SWAP, mc.player);
               mc.playerController.updateController();
               this.equipped = false;
               this.gotElytra = false;
            } else {
               int slot;
               if (this.silent.getValue()
                  && InventoryUtil.getItemHotbar(Items.ELYTRA) == -1
                  && (slot = InventoryUtil.findStackInventory(Items.ELYTRA)) != -1
                  && ogslot != -1) {
                  System.out.println(String.format("Moving %d to hotbar %d", slot, ogslot));
                  mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, ogslot, ClickType.SWAP, mc.player);
                  mc.playerController.updateController();
               }
            }
         }
      }
   }

   @Override
   public void onTick() {
      if (!fullNullCheck()) {
         Vec3d posVec;
         RayTraceResult result;
         if (this.mode.getValue() == NoFall.Mode.BUCKET
            && mc.player.fallDistance >= (float)this.distance.getValue().intValue()
            && !EntityUtil.isAboveWater(mc.player)
            && this.timer.passedMs(100L)
            && (result = mc.world.rayTraceBlocks(posVec = mc.player.getPositionVector(), posVec.add(0.0, -5.33F, 0.0), true, true, false))
               != null
            && result.typeOfHit == Type.BLOCK) {
            EnumHand hand = EnumHand.MAIN_HAND;
            if (mc.player.getHeldItemOffhand().getItem() == Items.WATER_BUCKET) {
               hand = EnumHand.OFF_HAND;
            } else if (mc.player.getHeldItemMainhand().getItem() != Items.WATER_BUCKET) {
               for(int i = 0; i < 9; ++i) {
                  if (mc.player.inventory.getStackInSlot(i).getItem() == Items.WATER_BUCKET) {
                     mc.player.inventory.currentItem = i;
                     mc.player.rotationPitch = 90.0F;
                     this.timer.reset();
                     return;
                  }
               }

               return;
            }

            mc.player.rotationPitch = 90.0F;
            mc.playerController.processRightClick(mc.player, mc.world, hand);
            this.timer.reset();
         }
      }
   }

   @Override
   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public static enum Mode {
      PACKET,
      BUCKET,
      ELYTRA;
   }

   public static enum State {
      FALL_CHECK {
         @Override
         public NoFall.State onSend(PacketEvent.Send event) {
            RayTraceResult result = Util.mc
               .world
               .rayTraceBlocks(Util.mc.player.getPositionVector(), Util.mc.player.getPositionVector().add(0.0, -3.0, 0.0), true, true, false);
            if (event.getPacket() instanceof CPacketPlayer
               && Util.mc.player.fallDistance >= 3.0F
               && result != null
               && result.typeOfHit == Type.BLOCK) {
               int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);
               if (slot != -1) {
                  Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, Util.mc.player);
                  NoFall.ogslot = slot;
                  Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, Action.START_FALL_FLYING));
                  return WAIT_FOR_ELYTRA_DEQUIP;
               } else {
                  return this;
               }
            } else {
               return this;
            }
         }
      },
      WAIT_FOR_ELYTRA_DEQUIP {
         @Override
         public NoFall.State onReceive(PacketEvent.Receive event) {
            return (NoFall.State)(!(event.getPacket() instanceof SPacketWindowItems) && !(event.getPacket() instanceof SPacketSetSlot) ? this : REEQUIP_ELYTRA);
         }
      },
      REEQUIP_ELYTRA {
         @Override
         public NoFall.State onUpdate() {
            Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, NoFall.ogslot, ClickType.SWAP, Util.mc.player);
            Util.mc.playerController.updateController();
            int slot = InventoryUtil.findStackInventory(Items.ELYTRA, true);
            if (slot == -1) {
               Command.sendMessage("§cElytra not found after regain?");
               return WAIT_FOR_NEXT_REQUIP;
            } else {
               Util.mc
                  .playerController
                  .windowClick(Util.mc.player.inventoryContainer.windowId, slot, NoFall.ogslot, ClickType.SWAP, Util.mc.player);
               Util.mc.playerController.updateController();
               NoFall.bypassTimer.reset();
               return RESET_TIME;
            }
         }
      },
      WAIT_FOR_NEXT_REQUIP {
         @Override
         public NoFall.State onUpdate() {
            return (NoFall.State)(NoFall.bypassTimer.passedMs(250L) ? REEQUIP_ELYTRA : this);
         }
      },
      RESET_TIME {
         @Override
         public NoFall.State onUpdate() {
            if (!Util.mc.player.onGround && !NoFall.bypassTimer.passedMs(250L)) {
               return this;
            } else {
               Util.mc
                  .player
                  .connection
                  .sendPacket(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.BEDROCK), (short)1337));
               return FALL_CHECK;
            }
         }
      };

      private State() {
      }

      public NoFall.State onSend(PacketEvent.Send e) {
         return this;
      }

      public NoFall.State onReceive(PacketEvent.Receive e) {
         return this;
      }

      public NoFall.State onUpdate() {
         return this;
      }
   }
}
