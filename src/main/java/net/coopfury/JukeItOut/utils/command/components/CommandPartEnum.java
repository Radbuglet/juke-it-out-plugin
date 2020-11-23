package net.coopfury.JukeItOut.utils.command.components;

import net.coopfury.JukeItOut.utils.command.CommandContext;
import net.coopfury.JukeItOut.utils.command.CommandPart;
import net.coopfury.JukeItOut.utils.command.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public abstract class CommandPartEnum<TSender extends CommandSender> implements CommandPart<TSender> {
    public final String id;

    public CommandPartEnum(String id) {
        this.id = id;
    }

    protected abstract Optional<CommandPart<TSender>> getHandler(String arg);
    protected abstract Iterable<String> getHandlers();

    @Override
    public CommandResult<TSender> handle(CommandContext<TSender> context) {
        Optional<String> arg = context.consumeArg();
        Optional<CommandPart<TSender>> handler = arg.flatMap(this::getHandler);

        if (!handler.isPresent()) {
            context.sendArgError(arg.isPresent() ? "Invalid sub command" : "Missing sub command", arg.isPresent() ? null : "<missing here>");

            StringBuilder listBuilder = new StringBuilder(ChatColor.RED + "Valid sub commands: " + ChatColor.WHITE);
            boolean first = true;
            for (String sub : getHandlers()) {
                if (first) {
                    first = false;
                } else {
                    listBuilder.append(" ,");
                }
                listBuilder.append(sub);
            }
            context.sender.sendMessage(listBuilder.toString());

            return CommandResult.finish(false);
        }

        context.putParsedArg(id, arg.get());
        return CommandResult.routeTo(handler.get());
    }
}
