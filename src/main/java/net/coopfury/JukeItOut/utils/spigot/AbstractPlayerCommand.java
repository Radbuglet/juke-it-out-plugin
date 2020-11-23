package net.coopfury.JukeItOut.utils.spigot;

import org.bukkit.ChatColor;
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
            commandSender.sendMessage(ChatColor.RED + "This command only operates on a player.");
            return false;
        }
    }
}
