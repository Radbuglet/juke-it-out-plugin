package net.coopfury.JukeItOut.state.game.playing;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.utils.spigot.BlockPointer;
import net.coopfury.JukeItOut.state.game.playing.teams.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class WorldReset {
    private final GameStatePlaying root;
    private final Set<BlockPointer> dirtyBlocks = new HashSet<>();

    public WorldReset(GameStatePlaying root) {
        this.root = root;
    }

    public void resetWorld(boolean resetBlocks) {
        Optional<World> world = Plugin.instance.config.getRoot().getGameWorld();
        if (!world.isPresent()) {
            Plugin.instance.getLogger().warning("Failed to reset world: no world (set by diamond spawn location) set.");
            return;
        }

        // Remove entities
        for (Item item: world.get().getEntitiesByClass(Item.class)) {
            item.remove();
        }

        for (EnderPearl pearl: world.get().getEntitiesByClass(EnderPearl.class)) {
            pearl.remove();
        }

        // Reset world (only happens on game reset)
        if (resetBlocks) {
            // Remove dirty blocks
            for (BlockPointer blockPointer : dirtyBlocks) {
                blockPointer.getBlock(world.get()).ifPresent(block -> block.setType(Material.AIR));
            }
            dirtyBlocks.clear();

            // Clear chests
            for (GameTeam team: root.teamManager.getTeams()) {
                Optional<Location> chestLocation = team.configTeam.getChestLocation();
                if (!chestLocation.isPresent()) continue;
                BlockState blockState = chestLocation.get().getBlock().getState();
                if (!(blockState instanceof Chest)) continue;
                ((Chest) blockState).getBlockInventory().clear();
                blockState.update(true);
            }
        }
    }

    public void onPlaceBlock(BlockPlaceEvent event) {
        if (event.getBlockReplacedState().getType() != Material.AIR) {
            event.setCancelled(true);
            return;
        }

        dirtyBlocks.add(new BlockPointer(event.getBlock()));
    }

    public boolean onBreakBlock(BlockBreakEvent event) {
        BlockPointer blockPointer = new BlockPointer(event.getBlock());

        if (!Plugin.instance.globalProtect.shouldAffect(event.getPlayer())
                || !root.teamManager.getMember(event.getPlayer().getUniqueId()).isPresent()) {
            dirtyBlocks.remove(blockPointer);
            return true;
        }

        if (!dirtyBlocks.contains(blockPointer)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can only break blocks placed by a player!");
            event.setCancelled(true);
            return false;
        }

        dirtyBlocks.remove(blockPointer);
        return true;
    }
}
