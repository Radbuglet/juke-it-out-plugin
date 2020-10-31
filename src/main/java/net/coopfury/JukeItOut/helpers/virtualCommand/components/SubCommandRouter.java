package net.coopfury.JukeItOut.helpers.virtualCommand.components;

import net.coopfury.JukeItOut.helpers.virtualCommand.ArgumentList;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandContext;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class SubCommandRouter<TSender extends CommandSender> implements VirtualCommandHandler<TSender> {
    private static class FallbackHandler implements VirtualCommandHandler<CommandSender> {
        @Override
        public boolean runCommand(CommandSender sender, ArgumentList args, CommandContext context) {
            args.rootOffset--;

            // Build summary
            {
                StringBuilder summary = new StringBuilder();
                summary.append(ChatColor.RED);
                summary.append("Error: Unknown sub-command in ");
                summary.append(ChatColor.GRAY);
                summary.append(args.getLeftStr(true));
                summary.append(" ");

                boolean first = true;
                if (args.getCount() == 0) {
                    summary.append(ChatColor.RED);
                    summary.append(ChatColor.UNDERLINE);
                    summary.append("<missing argument>");
                } else {
                    for (String part : args.iterateRight()) {
                        if (first) {
                            summary.append(ChatColor.RED);
                            summary.append(ChatColor.UNDERLINE);
                            summary.append(part);
                            summary.append(ChatColor.RED);
                            first = false;
                        } else {
                            summary.append(" ");
                            summary.append(part);
                        }
                    }
                }

                sender.sendMessage(summary.toString());
            }

            // Log valid sub commands
            {
                SubCommandRouter<?> router = context.getData(LAST_ROUTER).orElseThrow(IllegalStateException::new);
                StringBuilder subs = new StringBuilder(ChatColor.RED.toString());
                subs.append("Valid sub commands: ");
                subs.append(ChatColor.WHITE);

                if (router.handlerMap.size() == 0) {
                    subs.append("none?");
                } else {
                    boolean second = false;
                    for (String sub : router.handlerMap.keySet()) {
                        if (second) {
                            subs.append(", ");
                        }
                        second = true;
                        subs.append(sub);
                    }
                }
                sender.sendMessage(subs.toString());
            }

            args.rootOffset++;
            return false;
        }
    }

    private static final CommandContext.Symbol<SubCommandRouter<?>> LAST_ROUTER = new CommandContext.Symbol<>();
    public static final FallbackHandler defaultFallbackHandler = new FallbackHandler();

    private final VirtualCommandHandler<? super TSender> fallbackHandler;
    private final Map<String, VirtualCommandHandler<? super TSender>> handlerMap = new HashMap<>();

    public SubCommandRouter(VirtualCommandHandler<? super TSender> fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
    }

    public SubCommandRouter() {
        this.fallbackHandler = defaultFallbackHandler;
    }

    public SubCommandRouter<TSender> registerSub(String key, VirtualCommandHandler<? super TSender> sender) {
        if (handlerMap.containsKey(key)) {  // Not an assert because last time I checked, Spigot doesn't handle those well. :(
            throw new IllegalArgumentException(String.format("Registered the \"%s\" sub twice!", key));
        }
        handlerMap.put(key, sender);
        return this;
    }

    @Override
    public boolean runCommand(TSender sender, ArgumentList args, CommandContext context) {
        VirtualCommandHandler<? super TSender> handler = args.getCount() > 0 ?
                handlerMap.getOrDefault(args.getPart(0), fallbackHandler) : fallbackHandler;

        context.pushFrame();
        context.postData(LAST_ROUTER, this);
        args.rootOffset++;
        boolean result = handler.runCommand(sender, args, context);
        args.rootOffset--;
        context.popFrame();

        return result;
    }
}
