package net.coopfury.JukeItOut.modules.commands;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.node.GameNode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class CommandRegistrar extends GameNode {
    @Override
    protected void onEnterTree() {
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
