package net.coopfury.JukeItOut.modules.gameModule;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameStatePlaying;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

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
                if (currentState != null) currentState.onTick();
            }
        }.runTaskTimer(pluginInstance, 0, 0);

        GameStatePlaying state = new GameStatePlaying();
        for (Optional<ConfigTeam> teamConfig : configLoadingModule.root.getTeams().values()) {
            if (!teamConfig.isPresent() || !teamConfig.get().isValid()) {
                pluginInstance.getLogger().warning("Team is invalid!");
                continue;
            }
            GameTeam team = state.makeTeam(teamConfig.get());
            for (Player player: Bukkit.getOnlinePlayers()) {  // TODO: Temp
                team.addMember(state, player.getUniqueId());
            }
        }
        setGameState(state);
        state.startRound();
    }

    @Override
    protected void onDisable(Plugin pluginInstance) {
        if (currentState != null)
            currentState.onPluginDisable();
    }
}
