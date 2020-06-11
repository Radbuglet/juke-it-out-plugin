package net.coopfury.JukeItOut.helpers.spigot.command;

public interface SimpleCommandHandler<TParent, TSender> {
    boolean onCommand(TParent parent, TSender sender, ArgumentList args);
}
