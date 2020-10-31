package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.virtualCommand.PlayerCommandVirtualForward;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import net.coopfury.JukeItOut.helpers.virtualCommand.components.CommandLocationEditor;
import net.coopfury.JukeItOut.helpers.virtualCommand.components.SubCommandRouter;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import org.bukkit.entity.Player;

public class CommandConfMan extends PlayerCommandVirtualForward {
    private final SubCommandRouter<Player> router = new SubCommandRouter<>();

    public CommandConfMan() {
        ConfigLoadingModule config = Plugin.getModule(ConfigLoadingModule.class);
        router.registerSub("team", new SubCommandRouter<>());
        router.registerSub("loc", new SubCommandRouter<Player>()
            .registerSub("diamond_spawn", new CommandLocationEditor(
                    config.root::setDiamondSpawn, config.root::getDiamondSpawn)));
    }
    @Override
    protected VirtualCommandHandler<Player> getHandler() {
        return router;
    }
}
