package net.coopfury.JukeItOut.helpers.virtualCommand;

import org.bukkit.command.CommandSender;

public interface CommandPart<TSender extends CommandSender> {
    CommandResult<TSender> handle(CommandContext<TSender> context);
}
