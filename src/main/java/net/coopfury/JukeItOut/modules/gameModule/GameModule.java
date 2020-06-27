package net.coopfury.JukeItOut.modules.gameModule;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameStatePlaying;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameTeam;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

public class GameModule extends PluginModule {
    public GameState currentState;

    public void setGameState(GameState newState) {
        if (currentState != null) {
            HandlerList.unregisterAll(currentState);
        }
        currentState = newState;
        Plugin.getGame().bindListener(newState);
    }

    @Override
    protected void onEnable(Plugin pluginInstance) {
        ConfigLoadingModule configLoadingModule = Plugin.getModule(ConfigLoadingModule.class);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentState != null) currentState.tick();
            }
        }.runTaskTimer(pluginInstance, 0, 0);

        GameStatePlaying state = new GameStatePlaying();
        for (ConfigTeam teamConfig : configLoadingModule.root.getTeams().values()) {
            if (!teamConfig.isValid()) {
                pluginInstance.getLogger().warning("Team is invalid!");
                continue;
            }
            GameTeam team = state.makeTeam(teamConfig);
            team.addMember(state, pluginInstance.getServer().getOnlinePlayers().iterator().next().getUniqueId());  // TODO: Temp
        }
        setGameState(state);
        state.startRound();
    }
}
