package net.coopfury.JukeItOut.helpers.virtualCommand;

import net.coopfury.JukeItOut.helpers.spigot.AbstractPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public abstract class PlayerCommandVirtualForward extends AbstractPlayerCommand {
    protected abstract VirtualCommandHandler<Player> getHandler();

    @Override
    protected boolean onCommandPlayer(Player commandSender, Command command, String commandName, String[] args) {
        return getHandler().handleCommand(commandSender, new ArgumentList(commandName, args));
    }
}
