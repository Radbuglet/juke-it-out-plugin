package net.coopfury.JukeItOut.helpers.virtualCommand;

/**
 * A common interface for the bare minimum context-less virtual handling target.
 * Alternative VirtualCommandHandling interfaces can be made if extra metadata is needed.
 * @param <TSender>: The base type of the command sender.
 */
public interface VirtualCommandHandler<TSender> {
    boolean handleCommand(TSender sender, ArgumentList args);
}
