package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandRouter;
import net.coopfury.JukeItOut.helpers.virtualCommand.FixedArgCommand;
import net.coopfury.JukeItOut.helpers.virtualCommand.PlayerCommandVirtualForward;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import org.bukkit.entity.Player;

class CommandConfMan extends PlayerCommandVirtualForward {
    private static ConfigLoadingModule getConfig() {
        return Plugin.getModule(ConfigLoadingModule.class);
    }

    private static final CommandRouter<Player> router = new CommandRouter<Player>()
            .registerSub("save", new FixedArgCommand<>(new String[]{}, (parent, sender, router) -> {
                getConfig().saveConfig();
                sender.sendMessage(Constants.message_save_config_success);
                return true;
            }))
            .registerSub("reload", new FixedArgCommand<>(new String[]{}, (parent, sender, router) -> {
                getConfig().reloadConfig();
                sender.sendMessage(Constants.message_reload_config_success);
                return true;
            }));

    @Override
    protected VirtualCommandHandler<?, Player> getHandler() {
        return router;
    }
}
