package net.coopfury.JukeItOut.modules.gameModule;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameStatePlaying;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * The game module manages the swapping of game states.
 * There can only be one GameState at a time and any shared behavior is handled programmatically by this class. This
 * avoids the messy declarative systems necessary when trying to combine, group and/or nest multiple states.
 */
public class GameModule implements PluginModule {
    public GameState currentState;

    public void setGameState(GameState newState) {
        if (currentState != null) {
            currentState.onStateDisable();
            HandlerList.unregisterAll(currentState);
        }
        currentState = newState;
        Plugin.instance.bindListener(newState);
    }

    // Run loop handlers
    @Override
    public void onEnable(Plugin pluginInstance) {
        // Bind run loop
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentState != null)
                    currentState.onTick();
            }
        }.runTaskTimer(pluginInstance, 0, 0);

        // Setup initial game state
        ConfigLoadingModule configLoadingModule = Plugin.getModule(ConfigLoadingModule.class);
        GameStatePlaying state = new GameStatePlaying();

        // Make teams
        List<GameTeam> teams = new ArrayList<>();
        for (Optional<ConfigTeam> configTeam : configLoadingModule.root.getTeams().values()) {
            configTeam.ifPresent(team -> teams.add(state.makeTeam(team)));
        }

        // Add players to teams
        Iterator<GameTeam> teamPool = null;
        for (Player player: Bukkit.getOnlinePlayers()) {
            // Make team pool cycle
            if (teamPool == null || !teamPool.hasNext()) {
                teamPool = teams.iterator();
            }
            if (!teamPool.hasNext()) return;

            // Add player to the next team
            state.addPlayerToTeam(teamPool.next(), player);
        }

        // Start the game
        setGameState(state);
        state.startRound();
    }

    @Override
    public void onDisable(Plugin pluginInstance) {
        if (currentState != null)
            currentState.onPluginDisable();
    }

    // Global game event handlers
    private void appendPlayerName(StringBuilder builder, Player player, String displayName) {
        builder.append(UiUtils.formatVaultName(player, displayName));  // Chat color
    }

    private String formatActionMessage(Player player, String action) {
        StringBuilder builder = new StringBuilder();
        appendPlayerName(builder, player, player.getDisplayName());
        builder.append(" ").append(ChatColor.GRAY).append(action);
        return builder.toString();
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(formatActionMessage(event.getPlayer(), "joined the game."));
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(formatActionMessage(event.getPlayer(), "left the game."));
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        StringBuilder formatBuilder = new StringBuilder();
        if (currentState instanceof GameStatePlaying) {
            ((GameStatePlaying) currentState).formatAppendTeamName(formatBuilder, player);
        }

        appendPlayerName(formatBuilder, event.getPlayer(), "%s");
        formatBuilder.append(": ")  // Message separator
                .append(UiUtils.translateConfigText(Plugin.vaultChat.getPlayerSuffix(player)))  // Suffix (chat color)
                .append("%s"); // Message
        event.setFormat(formatBuilder.toString());
    }
}
