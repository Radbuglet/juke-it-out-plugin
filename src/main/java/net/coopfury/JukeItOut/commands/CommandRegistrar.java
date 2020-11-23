package net.coopfury.JukeItOut.commands;

import net.coopfury.JukeItOut.Plugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class CommandRegistrar {
    private CommandRegistrar() {}

    public static void bind() {
        registerCommand("fspeed", new CommandFlySpeed());
        registerCommand("confman", new CommandConfMan());
    }

    private static void registerCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = Plugin.instance.getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            Plugin.instance.getLogger().warning(String.format("Failed to register command \"%s\".", commandName));
        }
    }
}
