//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.features.modules.player;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.GameType;

public class FakePlayer extends Module {
   private final Setting<Integer> setHealth = this.register(new Setting<>("SetHealth", 20, 1, 20));
   private EntityOtherPlayerMP clonedPlayer;

   public FakePlayer() {
      super("FakePlayer", "Spawns a FakePlayer for testing", Module.Category.PLAYER, true, false, false);
   }

   @Override
   public void onEnable() {
      if (mc.player != null && !mc.player.isDead) {
         this.clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("a3ca166d-c5f1-3d5a-baac-b18a5b38d4cd"), "404"));
         this.clonedPlayer.copyLocationAndAnglesFrom(mc.player);
         this.clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
         this.clonedPlayer.rotationYaw = mc.player.rotationYaw;
         this.clonedPlayer.rotationPitch = mc.player.rotationPitch;
         this.clonedPlayer.inventory.copyInventory(mc.player.inventory);
         this.clonedPlayer.setGameType(GameType.SURVIVAL);
         this.clonedPlayer.setHealth((float)this.setHealth.getValue().intValue());
         mc.world.addEntityToWorld(-404, this.clonedPlayer);
         this.clonedPlayer.onLivingUpdate();
      } else {
         this.disable();
      }
   }

   @Override
   public void onDisable() {
      if (mc.world != null) {
         mc.world.removeEntityFromWorld(-404);
      }
   }
}
