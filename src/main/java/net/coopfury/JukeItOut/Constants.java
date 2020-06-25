package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import org.bukkit.ChatColor;

public final class Constants {
    public static final String permission_map_making = "juke_it_out.privilege.map_maker";
    public static final String command_fly_speed = "fspeed";
    public static final String command_conf_man = "confman";
    public static final UiUtils.TitleTimings title_timings_important = new UiUtils.TitleTimings(5, 20, 5);

    // Config
    public static final String message_add_team_success = ChatColor.GREEN + "Successfully added a new team!";
    public static final String message_duplicate_team_name = ChatColor.RED + "Duplicate team name!";
    public static final String message_unknown_team_name = ChatColor.RED + "Unknown team name!";
    public static final String message_team_removed_success = ChatColor.GREEN + "Removed team named " + ChatColor.WHITE + "%s" + ChatColor.GREEN +"!";
    public static final String message_team_list_none = ChatColor.RED + "No teams have been created!";
    public static final String message_team_list_some = ChatColor.GREEN + "Teams (%s):";
}
