package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.spigot.AbstractPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

class CommandFlySpeed extends AbstractPlayerCommand {
    private static final String fly_speed_reset_sub = "reset";
    private static final String message_fly_speed_help = ChatColor.RED + "Usage: /fspeed <speed: [0-1]|" + fly_speed_reset_sub + ">";
    private static final String message_fly_speed_success = ChatColor.GREEN + "Successfully set your fly speed.";
    private static final float default_fly_speed = 0.1f;

    @Override
    protected boolean onCommandPlayer(Player player, Command command, String s, String[] args) {
        if (args.length != 1) {
            player.sendMessage(message_fly_speed_help);
            return false;
        }

        if (args[0].equals(fly_speed_reset_sub)) {
            player.setFlySpeed(default_fly_speed);
            player.sendMessage(message_fly_speed_success);
            return true;
        }

        try {
            float new_speed = Float.parseFloat(args[0]);
            if (new_speed < 0 || new_speed > 1) {
                player.sendMessage(message_fly_speed_help);
                return false;
            }
            player.setFlySpeed(new_speed);
            player.sendMessage(message_fly_speed_success);
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(message_fly_speed_help);
            return false;
        }
    }
}
