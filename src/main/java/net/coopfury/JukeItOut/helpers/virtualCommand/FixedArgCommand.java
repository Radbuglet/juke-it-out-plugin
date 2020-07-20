package net.coopfury.JukeItOut.helpers.virtualCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class FixedArgCommand<TSender extends CommandSender> implements VirtualCommandHandler<TSender> {
    private final VirtualCommandHandler<TSender> handler;
    private String usageText = ChatColor.GOLD.toString();
    public int argCount;

    public FixedArgCommand(String[] usage, VirtualCommandHandler<TSender> handler) {
        this.handler = handler;
        addArgs(usage);
    }

    public void addArgs(String[] usage) {
        StringBuilder usageText = new StringBuilder();
        for (String usagePart : usage) {
            usageText.append(" <").append(usagePart).append(">");
        }
        this.usageText += usageText.toString();
        argCount += usage.length;
    }

    @Override
    public boolean handleCommand(TSender sender, ArgumentList args) {
        if (args.getCount() != argCount) {
            sender.sendMessage(VirtCommandUtils.formatUsageStart(args) + usageText);
            return false;
        }
        return handler.handleCommand(sender, args);
    }
}
