package net.coopfury.JukeItOut.helpers.virtualCommand;

public interface VirtualCommandHandler<TSender> {
    boolean runCommand(TSender sender, ArgumentList args, CommandContext context);
}
