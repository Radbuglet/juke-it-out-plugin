package net.coopfury.JukeItOut.listeners;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.state.game.playing.GameStatePlaying;
import net.coopfury.JukeItOut.utils.spigot.UiUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class GameEvents implements Listener {
    private Optional<GameStatePlaying> getPlayingState() {
        return Plugin.instance.gameManager.getActiveState(GameStatePlaying.class);
    }

    private String formatActionMessage(Player player, String action) {
        return UiUtils.formatVaultName(player, player.getDisplayName()) +
                " " + ChatColor.GRAY + action;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(formatActionMessage(event.getPlayer(), "joined the game."));
        getPlayingState().ifPresent(state -> state.teamManager.onMidGamePlayerJoin(event));
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(formatActionMessage(event.getPlayer(), "left the game."));
        getPlayingState().ifPresent(state -> state.teamManager.onMidGamePlayerQuit(event));
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        StringBuilder formatBuilder = new StringBuilder();
        Plugin.instance.gameManager.formatAppendPlayerName(formatBuilder, event.getPlayer(), UiUtils.formatVaultName(player, "%s"));
        formatBuilder.append(": ")  // Message separator
                .append(UiUtils.translateConfigText(Plugin.instance.vaultChat.getPlayerSuffix(player)))  // Suffix (chat color)
                .append("%s"); // Message
        event.setFormat(formatBuilder.toString());
    }

    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent event) {
        if (GlobalProtect.shouldAffect(event.getPlayer()))
            getPlayingState().ifPresent(state -> state.worldReset.onPlaceBlock(event));
    }

    @EventHandler
    private void onBreakBlock(BlockBreakEvent event) {
        getPlayingState().ifPresent(state -> state.worldReset.onBreakBlock(event));
    }

    @EventHandler
    private void onDamageByOther(EntityDamageByEntityEvent event) {
        getPlayingState().ifPresent(state -> state.combatManager.onEntityAttacked(event));
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        getPlayingState().ifPresent(state -> state.combatManager.onEntityDamaged(event));
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        getPlayingState().ifPresent(state -> state.teamManager.onBlockInteract(event));
    }
}
