package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.Constants;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class AdminCommandModule extends PluginModule {
    @Override
    protected void onEnable(Plugin pluginInstance) {
        super.onEnable(pluginInstance);
        registerCommand(pluginInstance, Constants.command_fly_speed, new CommandFlySpeed());
        registerCommand(pluginInstance, Constants.command_conf_man, new CommandConfMan());
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
