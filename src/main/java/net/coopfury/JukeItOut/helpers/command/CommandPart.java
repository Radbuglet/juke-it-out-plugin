package net.coopfury.JukeItOut.helpers.command;

import org.bukkit.command.CommandSender;

public interface CommandPart<TSender extends CommandSender> {
    CommandResult<TSender> handle(CommandContext<TSender> context);
}
