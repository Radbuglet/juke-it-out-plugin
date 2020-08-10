package net.coopfury.JukeItOut.helpers.spigot;

import net.coopfury.JukeItOut.Plugin;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class UiUtils {
    // Titles
    public static class TitleTimings {
        public final int in;
        public final int stay;
        public final int out;

        public TitleTimings(int in, int stay, int out) {
            this.in = in;
            this.stay = stay;
            this.out = out;
        }

        public PacketPlayOutTitle makePacket() {
            return new PacketPlayOutTitle(in, stay, out);
        }
    }

    public static IChatBaseComponent toNewChatFormat(String oldFormat) {
        return new ChatMessage(oldFormat);
    }

    public static void playTitle(Player player, String titleText, String subtitleText, TitleTimings timings) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        // Send title timings
        connection.sendPacket(timings.makePacket());

        // Send subtitle if applicable
        if (subtitleText != null)
            connection.sendPacket(new PacketPlayOutTitle(
                    PacketPlayOutTitle.EnumTitleAction.SUBTITLE, toNewChatFormat(subtitleText)));

        // Send title
        connection.sendPacket(new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE, toNewChatFormat(titleText)));
    }

    public static void playTitle(Player player, String titleText, TitleTimings timings) {
        playTitle(player, titleText, null, timings);
    }

    // Sounds
    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }

    // Vault
    public static String translateConfigText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String formatVaultName(Player player, String displayName) {
        return UiUtils.translateConfigText(Plugin.vaultChat.getPlayerPrefix(player)) + displayName;
    }

    public static String formatVaultName(Player player) {
        return formatVaultName(player, player.getDisplayName());
    }
}
