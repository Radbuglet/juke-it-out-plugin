package net.coopfury.JukeItOut.helpers.virtualCommand;

// TODO: Is knowledge of the parent still necessary when you have registerMultiple?
public interface VirtualCommandHandler<TParent, TSender> {
    boolean handleCommand(TParent parent, TSender sender, ArgumentList args);
}
