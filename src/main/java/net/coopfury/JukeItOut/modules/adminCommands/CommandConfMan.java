package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.helpers.spigot.command.AbstractPlayerCommand;
import net.coopfury.JukeItOut.helpers.spigot.command.ArgumentList;
import net.coopfury.JukeItOut.helpers.spigot.command.CommandRouter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandConfMan extends AbstractPlayerCommand {
    private final CommandRouter<Player> router = new CommandRouter<>((router, sender, args) -> {
        sender.sendMessage(ChatColor.RED + "Bad sub!");  // TODO
        return false;
    });

    @Override
    protected boolean onCommandPlayer(Player commandSender, Command command, String s, String[] args) {
        return router.onCommand(null, commandSender, new ArgumentList(args));
    }
}
