package net.coopfury.JukeItOut;

import org.bukkit.ChatColor;

public final class Constants {
    // Unsorted
    public static final String message_non_player_command = ChatColor.RED + "This command only operates on a player.";
    public static final String message_crafting_disabled_item = ChatColor.RED + "Crafting disabled";
    public static final String permission_map_making = "juke_it_out.privilege.map_maker";

    // Fly speed
    public static final String command_fly_speed = "fspeed";
    public static final String fly_speed_reset_sub = "reset";
    public static final String message_fly_speed_help = ChatColor.RED + "Usage: /fspeed <speed: [0-1]|" + fly_speed_reset_sub + ">";
    public static final String message_fly_speed_success = ChatColor.GREEN + "Successfully set your fly speed.";
    public static final float default_fly_speed = 0.1f;

    // Config
    public static final String locations_root = "locations";

    // Game messages
    public static final String message_game_round_title = ChatColor.RED + "Round %s";
}
