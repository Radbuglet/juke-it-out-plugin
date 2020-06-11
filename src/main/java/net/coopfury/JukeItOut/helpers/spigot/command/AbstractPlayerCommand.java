package net.coopfury.JukeItOut.helpers.spigot.command;

import net.coopfury.JukeItOut.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractPlayerCommand implements CommandExecutor {
    protected abstract boolean onCommandPlayer(Player commandSender, Command command, String s, String[] args);

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            return onCommandPlayer((Player) commandSender, command, s, args);
        } else {
            commandSender.sendMessage(Constants.message_non_player_command);
            return false;
        }
    }
}
