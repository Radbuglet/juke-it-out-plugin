package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.spigot.PlayerCommandVirtualForward;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandRouter;
import net.coopfury.JukeItOut.helpers.virtualCommand.FixedArgCommand;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtCommandUtils;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class CommandConfMan extends PlayerCommandVirtualForward {
    private static ConfigLoadingModule getConfig() {
        return Plugin.getModule(ConfigLoadingModule.class);
    }

    private static final CommandRouter<Player> router = new CommandRouter<Player>()
            .registerSub("teams", new CommandRouter<Player>()
                    .registerMultiple(router -> VirtCommandUtils.registerMapEditingSubs(
                            router, getConfig().root.getTeams()
                    )))
            .registerSub("save", new FixedArgCommand<>(new String[]{}, (parent, sender, router) -> {
                getConfig().saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Saved config!");
                return true;
            }))
            .registerSub("reload", new FixedArgCommand<>(new String[]{}, (parent, sender, router) -> {
                getConfig().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Reloaded config!");
                return true;
            }));

    @Override
    protected VirtualCommandHandler<?, Player> getHandler() {
        return router;
    }
}
