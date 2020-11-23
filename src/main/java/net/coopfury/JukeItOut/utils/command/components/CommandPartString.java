package net.coopfury.JukeItOut.utils.command.components;

import net.coopfury.JukeItOut.utils.command.CommandContext;
import net.coopfury.JukeItOut.utils.command.CommandPart;
import net.coopfury.JukeItOut.utils.command.CommandResult;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class CommandPartString<TSender extends CommandSender> implements CommandPart<TSender> {
    public final String id;
    private final CommandPart<TSender> nextHandler;

    public CommandPartString(String id, CommandPart<TSender> nextHandler) {
        this.id = id;
        this.nextHandler = nextHandler;
    }

    @Override
    public CommandResult<TSender> handle(CommandContext<TSender> context) {
        Optional<String> arg = context.consumeArg();
        if (!arg.isPresent()) {
            context.sendArgError("Missing argument named \"" + id + "\"", "<" + id + " (missing)>");
            return CommandResult.finish(false);
        }

        context.putParsedArg(id, arg.get());
        return CommandResult.routeTo(nextHandler);
    }
}
