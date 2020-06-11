package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

public final class Constants {
    // Unsorted
    public static final String message_non_player_command = ChatColor.RED + "This command only operates on a player.";
    public static final String message_usage_top_start = ChatColor.RED + "Usage: " + ChatColor.WHITE;
    public static final String message_usage_top_end = ChatColor.GOLD + " <?>";
    public static final String message_usage_sub = ChatColor.GOLD + "- ";
    public static final String message_crafting_disabled_item = ChatColor.RED + "Crafting disabled";
    public static final String permission_map_making = "juke_it_out.privilege.map_maker";

    // Fly speed
    public static final String command_fly_speed = "fspeed";
    public static final String fly_speed_reset_sub = "reset";
    public static final String message_fly_speed_help = ChatColor.RED + "Usage: /fspeed <speed: [0-1]|" + fly_speed_reset_sub + ">";
    public static final String message_fly_speed_success = ChatColor.GREEN + "Successfully set your fly speed.";
    public static final float default_fly_speed = 0.1f;

    // Config
    public static final String config_root_teams = "teams";
    public static final String command_conf_man = "confman";
    public static final String message_add_team_success = ChatColor.GREEN + "Successfully added a new team!";

    // Game
    public static final String message_game_new_round = ChatColor.RED + "Round %s";
    public static final Sound sound_new_round = Sound.ENDERDRAGON_HIT;
    public static final UiUtils.TitleTimings title_timings_important = new UiUtils.TitleTimings(5, 20, 5);
}
