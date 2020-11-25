package net.coopfury.JukeItOut.listeners;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.state.game.playing.GameStatePlaying;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class GameEvents implements Listener {
    private Optional<GameStatePlaying> getPlayingState() {
        return Plugin.instance.gameManager.getActiveState(GameStatePlaying.class);
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
