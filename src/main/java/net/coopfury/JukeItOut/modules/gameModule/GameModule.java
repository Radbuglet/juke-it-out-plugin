package net.coopfury.JukeItOut.modules.gameModule;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.gameModule.lobby.GameStateLobby;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameStatePlaying;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

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

        // Set initial state
        setGameState(new GameStateLobby());
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
