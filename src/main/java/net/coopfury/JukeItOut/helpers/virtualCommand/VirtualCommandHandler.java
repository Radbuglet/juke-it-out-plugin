package net.coopfury.JukeItOut.helpers.virtualCommand;

public interface VirtualCommandHandler<TParent, TSender> {
    boolean handleCommand(TParent parent, TSender sender, ArgumentList args);
}
