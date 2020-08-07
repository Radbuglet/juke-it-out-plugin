package net.coopfury.JukeItOut.modules.gameModule;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameStatePlaying;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

/**
 * The game module manages the swapping of game states.
 * There can only be one GameState at a time and any shared behavior is handled programmatically by this class. This
 * avoids the messy declarative systems necessary when trying to combine, group and/or nest multiple states.
 */
public class GameModule extends PluginModule {
    public GameState currentState;

    public void setGameState(GameState newState) {
        if (currentState != null)
            HandlerList.unregisterAll(currentState);
        currentState = newState;
        Plugin.instance.bindListener(newState);
    }

    // Run loop handlers
    @Override
    protected void onEnable(Plugin pluginInstance) {
        // Bind run loop
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentState != null)
                    currentState.onTick();
            }
        }.runTaskTimer(pluginInstance, 0, 0);

        // Setup initial game state  TODO: Temp
        ConfigLoadingModule configLoadingModule = Plugin.getModule(ConfigLoadingModule.class);
        GameStatePlaying state = new GameStatePlaying();
        for (Optional<ConfigTeam> teamConfig : configLoadingModule.root.getTeams().values()) {
            if (!teamConfig.isPresent() || !teamConfig.get().isValid()) {
                pluginInstance.getLogger().warning("Team is invalid!");
                continue;
            }
            GameTeam team = state.makeTeam(teamConfig.get());
            for (Player player: Bukkit.getOnlinePlayers()) {
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

    // Global game event handlers (mostly delegates to current state after some mandatory preliminary handling)
    // TODO: Join and leave
    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        StringBuilder formatBuilder = new StringBuilder();
        if (currentState instanceof GameStatePlaying) {
            ((GameStatePlaying) currentState).formatAppendTeamName(formatBuilder, player);
        }

        formatBuilder.append(UiUtils.formatVaultName(player, "%s")).append(": ")  // Name
            .append(UiUtils.translateConfigText(Plugin.vaultChat.getPlayerSuffix(player)))  // Chat color
                .append("%s");  // Message

        event.setFormat(formatBuilder.toString());
    }
}
