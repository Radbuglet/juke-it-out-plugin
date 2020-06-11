package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandRouter;
import net.coopfury.JukeItOut.helpers.virtualCommand.PlayerCommandVirtualForward;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import org.bukkit.entity.Player;

class CommandConfMan extends PlayerCommandVirtualForward {
    private static final CommandRouter<Player> router = new CommandRouter<Player>()
            .registerSub("teams", new CommandRouter<Player>()
                .registerSub("add", (router, sender, args) -> {
                    sender.sendMessage(Constants.message_add_team_success);
                    return true;
                })
                .registerSub("list", (router, sender, args) -> {
                    sender.sendMessage("Not implemented");
                    return true;
                })
            );

    @Override
    protected VirtualCommandHandler<?, Player> getHandler() {
        return router;
    }
}
