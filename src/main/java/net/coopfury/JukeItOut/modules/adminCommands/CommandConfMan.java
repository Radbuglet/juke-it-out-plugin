package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandRouter;
import net.coopfury.JukeItOut.helpers.virtualCommand.FixedArgCommand;
import net.coopfury.JukeItOut.helpers.virtualCommand.PlayerCommandVirtualForward;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.SchemaTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

class CommandConfMan extends PlayerCommandVirtualForward {
    private static ConfigLoadingModule getConfig() {
        return Plugin.getModule(ConfigLoadingModule.class);
    }

    private static final CommandRouter<Player> router = new CommandRouter<Player>()
            .registerSub("teams", new CommandRouter<Player>()
                .registerSub("add", new FixedArgCommand<>(new String[]{ "name" }, (router, sender, args) -> {
                    if (getConfig().getTeamIndexByName(args.getPart(0)) != -1) {
                        sender.sendMessage(Constants.message_duplicate_team_name);
                        return false;
                    }
                    getConfig().teams.add(new SchemaTeam(
                            args.getPart(0), 0,
                            sender.getLocation().clone()));
                    sender.sendMessage(Constants.message_add_team_success);
                    return true;
                }))
                .registerSub("delete", new FixedArgCommand<>(new String[]{ "name" }, (router, sender, args) -> {
                    int targetIndex = getConfig().getTeamIndexByName(args.getPart(0));
                    if (targetIndex == -1) {
                        sender.sendMessage(Constants.message_unknown_team_name);
                        return false;
                    }
                    getConfig().teams.remove(targetIndex);
                    sender.sendMessage(String.format(Constants.message_team_removed_success, args.getPart(0)));
                    return true;
                }))
                .registerSub("list", (router, sender, args) -> {
                    List<SchemaTeam> teams = getConfig().teams;
                    if (teams.size() == 0) {
                        sender.sendMessage(Constants.message_team_list_none);
                        return false;
                    } else {
                        sender.sendMessage(String.format(Constants.message_team_list_some, teams.size()));
                        for (SchemaTeam team: teams) {
                            sender.sendMessage(ChatColor.GREEN + "- " + team.name);
                        }
                        return true;
                    }
                })
            ).registerSub("reload", (router, sender, args) -> {
                sender.sendMessage(Constants.message_reload_config_success);
                getConfig().reloadConfig();
                return true;
            });

    @Override
    protected VirtualCommandHandler<?, Player> getHandler() {
        return router;
    }
}
