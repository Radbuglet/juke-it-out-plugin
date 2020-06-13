package net.coopfury.JukeItOut.helpers.virtualCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class FixedArgCommand<TParent, TSender extends CommandSender> implements VirtualCommandHandler<TParent, TSender> {
    private final VirtualCommandHandler<TParent, TSender> handler;
    private final String usageText;
    public final int argCount;

    public FixedArgCommand(String[] usage, VirtualCommandHandler<TParent, TSender> handler) {
        this.handler = handler;
        argCount = usage.length;
        StringBuilder usageText = new StringBuilder(ChatColor.GOLD.toString());
        for (String usagePart : usage) {
            usageText.append(" <").append(usagePart).append(">");
        }
        this.usageText = usageText.toString();
    }

    @Override
    public boolean handleCommand(TParent parent, TSender sender, ArgumentList args) {
        if (args.getCount() != argCount) {
            sender.sendMessage(VirtCommandUtils.formatUsageStart(args) + usageText);
            return false;
        }
        return handler.handleCommand(parent, sender, args);
    }
}
