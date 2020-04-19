package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Game;
import net.coopfury.JukeItOut.GameModule;
import net.coopfury.JukeItOut.Constants;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class AdminCommandModule extends GameModule {
    @Override
    protected void onEnable(Game pluginInstance) {
        super.onEnable(pluginInstance);
        registerCommand(pluginInstance, Constants.command_fly_speed, new CommandFlySpeed());
    }

    private void registerCommand(Game pluginInstance, String commandName, CommandExecutor executor) {
        PluginCommand command = pluginInstance.getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            pluginInstance.getLogger().warning(String.format("Failed to register command \"%s\".", commandName));
        }
    }
}
