package net.coopfury.JukeItOut.helpers.virtualCommand.components;

import net.coopfury.JukeItOut.helpers.virtualCommand.ArgumentList;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandContext;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FixedCommandValidator<TSender extends CommandSender> implements VirtualCommandHandler<TSender> {
    private final List<String> usage;
    private final VirtualCommandHandler<TSender> handler;

    public FixedCommandValidator(List<String> usage, VirtualCommandHandler<TSender> handler) {
        this.usage = usage;
        this.handler = handler;
    }

    @Override
    public boolean runCommand(TSender sender, ArgumentList args, CommandContext context) {
        if (args.getCount() != usage.size()) {
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.RED);
            builder.append("Usage: ");
            builder.append(args.getLeftStr(true));
            for (String part : usage) {
                builder.append(" <");
                builder.append(part);
                builder.append(">");
            }
            sender.sendMessage(builder.toString());
            return false;
        }
        return false;
    }
}
