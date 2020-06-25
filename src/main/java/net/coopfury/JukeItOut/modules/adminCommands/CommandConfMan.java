package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.config.ConfigDictionary;
import net.coopfury.JukeItOut.helpers.spigot.PlayerCommandVirtualForward;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandRouter;
import net.coopfury.JukeItOut.helpers.virtualCommand.FixedArgCommand;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtCommandUtils;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class CommandConfMan extends PlayerCommandVirtualForward {
    private static ConfigLoadingModule getConfig() {
        return Plugin.getModule(ConfigLoadingModule.class);
    }

    private static final CommandRouter<Player> router = new CommandRouter<Player>()
            .registerSub("teams", new CommandRouter<Player>()
                    .registerMultiple(router -> {
                        ConfigDictionary<?> map = getConfig().root.getTeams();
                        VirtCommandUtils.registerMapEditingSubs(router, map);
                        VirtCommandUtils.registerMapAdder(router, map, new String[]{ "color" }, (sender, name, args) -> {
                            ConfigTeam team = new ConfigTeam();
                            team.setName(name);
                            team.setSpawnLocation(sender.getLocation());
                            team.setWoolColor(0);  // TODO: Use arg
                            return team.map;
                        });
                    }))
            .registerSub("save", new FixedArgCommand<>(new String[]{}, (parent, sender, args) -> {
                getConfig().saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Saved config!");
                return true;
            }))
            .registerSub("reload", new FixedArgCommand<>(new String[]{}, (parent, sender, args) -> {
                getConfig().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Reloaded config!");
                return true;
            }));

    @Override
    protected VirtualCommandHandler<?, Player> getHandler() {
        return router;
    }
}
