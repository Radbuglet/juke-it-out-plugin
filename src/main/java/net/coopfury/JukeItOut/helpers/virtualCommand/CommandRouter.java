package net.coopfury.JukeItOut.helpers.virtualCommand;

import net.coopfury.JukeItOut.Constants;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CommandRouter<TSender extends CommandSender> implements VirtualCommandHandler<TSender> {
    private final VirtualCommandHandler<? super TSender> defaultHandler;
    private final Map<String, VirtualCommandHandler<? super TSender>> handlers = new HashMap<>();

    public CommandRouter(VirtualCommandHandler<? super TSender> defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    public CommandRouter() {
        this.defaultHandler = (sender, args) -> {
            if (args.getCount() > 0) {
                sender.sendMessage(ChatColor.RED + "Unknown sub command: " + ChatColor.GOLD + args.getPart(0));
            } else {
                sender.sendMessage(ChatColor.RED + "Command is not complete.");
            }
            sender.sendMessage(VirtCommandUtils.formatUsageStart(args) + ChatColor.GOLD + " <...>");
            for (String sub : getSubs()) {
                sender.sendMessage(Constants.message_list_prefix + sub);
            }
            return false;
        };
    }

    public Iterable<String> getSubs() {
        return handlers.keySet();
    }

    public CommandRouter<TSender> registerSub(String name, VirtualCommandHandler<? super TSender> handler) {
        handlers.put(name, handler);
        return this;
    }

    public CommandRouter<TSender> registerMultiple(Consumer<CommandRouter<TSender>> registrar) {
        registrar.accept(this);
        return this;
    }

    @Override
    public boolean handleCommand(TSender sender, ArgumentList args) {
        VirtualCommandHandler<? super TSender> handler = args.getCount() == 0 ? defaultHandler
                : handlers.getOrDefault(args.getPart(0), defaultHandler);

        if (handler != defaultHandler)
            args.rootOffset++;
        boolean result = handler.handleCommand(sender, args);
        if (handler != defaultHandler)
            args.rootOffset--;
        return result;
    }
}
