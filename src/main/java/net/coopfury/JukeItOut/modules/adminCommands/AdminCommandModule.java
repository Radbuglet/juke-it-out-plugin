package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class AdminCommandModule implements PluginModule {
    @Override
    public void onEnable(Plugin pluginInstance) {
        registerCommand(pluginInstance, "fspeed", new CommandFlySpeed());
        registerCommand(pluginInstance, "confman", new CommandConfMan());
    }

    private void registerCommand(Plugin pluginInstance, String commandName, CommandExecutor executor) {
        PluginCommand command = pluginInstance.getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            pluginInstance.getLogger().warning(String.format("Failed to register command \"%s\".", commandName));
        }
    }
}
