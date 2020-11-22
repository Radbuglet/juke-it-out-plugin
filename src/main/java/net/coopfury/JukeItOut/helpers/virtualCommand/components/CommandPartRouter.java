package net.coopfury.JukeItOut.helpers.virtualCommand.components;

import net.coopfury.JukeItOut.helpers.virtualCommand.CommandPart;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandPartRouter<TSender extends CommandSender> extends CommandPartEnum<TSender> {
    private final Map<String, CommandPart<TSender>> handlers = new HashMap<>();

    public CommandPartRouter(String id) {
        super(id);
    }

    public CommandPartRouter<TSender> registerPart(String name, CommandPart<TSender> handler) {
        handlers.put(name, handler);
        return this;
    }

    @Override
    protected Optional<CommandPart<TSender>> getHandler(String arg) {
        return Optional.ofNullable(handlers.get(arg));
    }

    @Override
    protected Iterable<String> getHandlers() {
        return handlers.keySet();
    }
}
