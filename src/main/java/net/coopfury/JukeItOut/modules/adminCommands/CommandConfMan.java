package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.helpers.spigot.AbstractPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandConfMan extends AbstractPlayerCommand {
    @Override
    protected boolean onCommandPlayer(Player commandSender, Command command, String s, String[] args) {
        return false;
    }
}
