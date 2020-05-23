package net.coopfury.JukeItOut.helpers.spigot;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class UiUtils {
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
        // FIXME: Someone could exploit this by adding `"`s and escape sequences!!!
        return IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + oldFormat + "\"}");
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

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }
}
