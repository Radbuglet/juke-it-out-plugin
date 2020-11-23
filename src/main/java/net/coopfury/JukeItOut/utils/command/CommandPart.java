package net.coopfury.JukeItOut.utils.command;

import org.bukkit.command.CommandSender;

public interface CommandPart<TSender extends CommandSender> {
    CommandResult<TSender> handle(CommandContext<TSender> context);
}
