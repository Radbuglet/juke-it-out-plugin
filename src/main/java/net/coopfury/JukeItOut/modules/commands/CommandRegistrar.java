package net.coopfury.JukeItOut.modules.commands;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.java.signal.SignalPriority;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class CommandRegistrar {
    public CommandRegistrar() {
        Plugin.instance.onEnable.connect(this::onEnterTree, SignalPriority.Low);
    }

    private void onEnterTree(Plugin plugin) {
        registerCommand("fspeed", new CommandFlySpeed());
        registerCommand("confman", new CommandConfMan());
    }

    private void registerCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = Plugin.instance.getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            Plugin.instance.getLogger().warning(String.format("Failed to register command \"%s\".", commandName));
        }
    }
}
