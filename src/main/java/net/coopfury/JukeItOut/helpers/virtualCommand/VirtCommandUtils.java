package net.coopfury.JukeItOut.helpers.virtualCommand;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

public final class VirtCommandUtils {
    // Misc
    public static String formatUsageStart(ArgumentList list) {
        return ChatColor.RED + "Usage: " + ChatColor.WHITE + list.getLeftStr(true);
    }

    public static Optional<Location> getTargetBlockReasoned(Player player, int range) {
        Block block = player.getTargetBlock((Set<Material>) null, range);
        if (block == null) {
            player.sendMessage(ChatColor.RED + "You must be looking at a block!");
            return Optional.empty();
        }

        return Optional.of(block.getLocation());
    }
}
