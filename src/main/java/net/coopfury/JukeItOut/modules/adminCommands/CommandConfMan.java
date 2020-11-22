package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.helpers.spigot.AbstractPlayerCommand;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandContext;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandResult;
import net.coopfury.JukeItOut.helpers.virtualCommand.components.CommandPartRouter;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandConfMan extends AbstractPlayerCommand {
    private final CommandPartRouter<Player> router = new CommandPartRouter<>("__root");

    public CommandConfMan() {
        router.registerPart("loc", (ctx) -> {
            ctx.sender.sendMessage("Yay!");
            return CommandResult.finish(true);
        });
    }

    @Override
    protected boolean onCommandPlayer(Player commandSender, Command command, String name, String[] args) {
        return CommandContext.run(commandSender, name, args, router);
    }
}
