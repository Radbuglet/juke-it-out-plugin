package net.coopfury.JukeItOut.utils.command;

import org.bukkit.command.CommandSender;

public class CommandResult<TSender extends CommandSender> {
    final boolean isFinished;
    final Object value;

    private CommandResult(boolean isFinished, Object value) {
        this.isFinished = isFinished;
        this.value = value;
    }

    public static<TSender extends CommandSender> CommandResult<TSender> finish(boolean success) {
        return new CommandResult<>(true, success);
    }

    public static<TSender extends CommandSender> CommandResult<TSender> routeTo(CommandPart<TSender> part) {
        return new CommandResult<>(false, part);
    }
}