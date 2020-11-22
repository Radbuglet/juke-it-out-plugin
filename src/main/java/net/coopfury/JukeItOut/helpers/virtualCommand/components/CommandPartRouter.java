package net.coopfury.JukeItOut.helpers.virtualCommand.components;

import net.coopfury.JukeItOut.helpers.virtualCommand.CommandContext;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandPart;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandPartRouter<TSender extends CommandSender> implements CommandPart<TSender> {
    public final String id;
    private final Map<String, CommandPart<TSender>> handlers = new HashMap<>();

    public CommandPartRouter(String id) {
        this.id = id;
    }

    @Override
    public CommandResult<TSender> handle(CommandContext<TSender> context) {
        Optional<String> arg = context.consumeArg();

        if (!arg.isPresent()) {
            context.sendArgError("Missing sub command", "<missing sub command>");
            return CommandResult.finish(false);
        }

        CommandPart<TSender> handler = handlers.get(arg.get());
        if (handler == null) {
            context.sendArgError("Invalid sub command");

            StringBuilder listBuilder = new StringBuilder(ChatColor.RED + "Valid sub commands: ");
            boolean first = true;
            for (String sub : handlers.keySet()) {
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
        return CommandResult.routeTo(handler);
    }

    public void registerPart(String name, CommandPart<TSender> handler) {
        handlers.put(name, handler);
    }
}
