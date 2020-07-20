package net.coopfury.JukeItOut.helpers.virtualCommand;

public interface VirtualCommandHandler<TSender> {
    boolean handleCommand(TSender sender, ArgumentList args);
}
