package net.coopfury.JukeItOut.helpers.spigot.command;

import java.util.HashMap;
import java.util.Map;

public class CommandRouter<TSender> implements SimpleCommandHandler<Object, TSender> {
    private SimpleCommandHandler<CommandRouter<TSender>, TSender> fallbackHandler;
    private Map<String, SimpleCommandHandler<CommandRouter<TSender>, TSender>> handlers = new HashMap<>();

    public CommandRouter(SimpleCommandHandler<CommandRouter<TSender>, TSender> fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
    }

    public void registerSub(String name, SimpleCommandHandler<CommandRouter<TSender>, TSender> handler) {
        handlers.put(name, handler);
    }

    public Iterable<String> getSubs() {
        return handlers.keySet();
    }

    @Override
    public boolean onCommand(Object parent, TSender sender, ArgumentList args) {
        if (args.getArgCount() == 0)
            return fallbackHandler.onCommand(this, sender, args);
        SimpleCommandHandler<CommandRouter<TSender>, TSender> handler = handlers.getOrDefault(args.getArg(0), null);
        if (handler == null)
            return fallbackHandler.onCommand(this, sender, args);
        args.rootOffset++;
        return handler.onCommand(this, sender, args);
    }
}
