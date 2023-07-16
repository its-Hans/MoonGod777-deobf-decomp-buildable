//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.moon.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import me.moon.features.Feature;
import me.moon.features.setting.Setting;
import me.moon.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

public class FriendManager extends Feature {
   private List<FriendManager.Friend> friends = new ArrayList<>();

   public FriendManager() {
      super("Friends");
   }

   public boolean isFriend(String name) {
      this.cleanFriends();
      return this.friends.stream().anyMatch(friend -> friend.username.equalsIgnoreCase(name));
   }

   public boolean isFriend(EntityPlayer player) {
      return this.isFriend(player.getName());
   }

   public void addFriend(String name) {
      FriendManager.Friend friend = this.getFriendByName(name);
      if (friend != null) {
         this.friends.add(friend);
      }

      this.cleanFriends();
   }

   public void removeFriend(String name) {
      this.cleanFriends();

      for(FriendManager.Friend friend : this.friends) {
         if (friend.getUsername().equalsIgnoreCase(name)) {
            this.friends.remove(friend);
            break;
         }
      }
   }

   public void onLoad() {
      this.friends = new ArrayList<>();
      this.clearSettings();
   }

   public void saveFriends() {
      this.clearSettings();
      this.cleanFriends();

      for(FriendManager.Friend friend : this.friends) {
         this.register(new Setting<>(friend.getUuid().toString(), friend.getUsername()));
      }
   }

   public void cleanFriends() {
      this.friends.stream().filter(Objects::nonNull).filter(friend -> friend.getUsername() != null);
   }

   public List<FriendManager.Friend> getFriends() {
      this.cleanFriends();
      return this.friends;
   }

   public FriendManager.Friend getFriendByName(String input) {
      UUID uuid = PlayerUtil.getUUIDFromName(input);
      return uuid != null ? new FriendManager.Friend(input, uuid) : null;
   }

   public void addFriend(FriendManager.Friend friend) {
      this.friends.add(friend);
   }

   public static class Friend {
      private final String username;
      private final UUID uuid;

      public Friend(String username, UUID uuid) {
         this.username = username;
         this.uuid = uuid;
      }

      public String getUsername() {
         return this.username;
      }

      public UUID getUuid() {
         return this.uuid;
      }
   }
}
