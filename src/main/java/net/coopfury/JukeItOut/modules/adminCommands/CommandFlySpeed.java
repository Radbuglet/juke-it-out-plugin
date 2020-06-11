package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.spigot.command.AbstractPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandFlySpeed extends AbstractPlayerCommand {
    @Override
    protected boolean onCommandPlayer(Player player, Command command, String s, String[] args) {
        if (args.length != 1) {
            player.sendMessage(Constants.message_fly_speed_help);
            return false;
        }

        if (args[0].equals(Constants.fly_speed_reset_sub)) {
            player.setFlySpeed(Constants.default_fly_speed);
            player.sendMessage(Constants.message_fly_speed_success);
            return true;
        }

        try {
            float new_speed = Float.parseFloat(args[0]);
            if (new_speed < 0 || new_speed > 1) {
                player.sendMessage(Constants.message_fly_speed_help);
                return false;
            }
            player.setFlySpeed(new_speed);
            player.sendMessage(Constants.message_fly_speed_success);
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(Constants.message_fly_speed_help);
            return false;
        }
    }
}
