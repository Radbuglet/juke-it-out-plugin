package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.spigot.AbstractPlayerCommand;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.SchemaTeam;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandAddTeam extends AbstractPlayerCommand {
    @Override
    protected boolean onCommandPlayer(Player commandSender, Command command, String s, String[] args) {
        Plugin.getModule(ConfigLoadingModule.class).teams.add(
                new SchemaTeam("Red", 3, commandSender.getLocation()));
        commandSender.sendMessage(Constants.message_add_team_success);
        return true;
    }
}
