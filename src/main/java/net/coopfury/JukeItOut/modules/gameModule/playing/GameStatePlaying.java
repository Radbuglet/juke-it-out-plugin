package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.java.TimeUnits;
import net.coopfury.JukeItOut.helpers.java.TimestampUtils;
import net.coopfury.JukeItOut.helpers.spigot.BlockPointer;
import net.coopfury.JukeItOut.helpers.spigot.PlayerUtils;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.GlobalFixesModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.gameModule.GameState;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.GameTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.GameTeamMember;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GameStatePlaying implements GameState {
    private final Set<BlockPointer> dirtyBlocks = new HashSet<>();

    // Round state
    private int roundId;
    private long roundEndTime;
    private boolean spawnedDiamond;

    // Round management
    public void startRound() {
        // Reset game state
        roundId++;
        roundEndTime = TimestampUtils.getTimeIn(TimeUnits.Secs, 30);
        spawnedDiamond = false;

        // Reset characters
        for (GameTeam team: teams) {
            DyeColor color = team.configTeam.getWoolColor().orElse(DyeColor.WHITE);
            for (GameTeamMember member: team.members) {
                member.resetCharacter(roundId, color);
            }
        }

        // Reset world
        resetWorld(false);
    }

    private void resetWorld(boolean resetBlocks) {
        Optional<World> world = Plugin.getModule(ConfigLoadingModule.class).root.getGameWorld();
        if (!world.isPresent()) {
            Plugin.instance.getLogger().warning("Failed to reset world: no world (set by diamond spawn location) set.");
            return;
        }

        for (Item item: world.get().getEntitiesByClass(Item.class)) {
            item.remove();
        }

        if (resetBlocks) {
            for (BlockPointer blockPointer : dirtyBlocks) {
                blockPointer.getBlock(world.get()).ifPresent(block -> block.setType(Material.AIR));
            }
            dirtyBlocks.clear();
        }
    }

    // Team management

    // Block handling
    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent event) {
        if (!GlobalFixesModule.shouldDenyMapMakePrivilege(event.getPlayer())) {
            return;
        }

        if (!getMember(event.getPlayer()).isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlockReplacedState().getType() != Material.AIR) {
            event.setCancelled(true);
            return;
        }

        dirtyBlocks.add(new BlockPointer(event.getBlock()));
    }
    
    @EventHandler
    private void onBreakBlock(BlockBreakEvent event) {
        BlockPointer blockPointer = new BlockPointer(event.getBlock());

        if (!GlobalFixesModule.shouldDenyMapMakePrivilege(event.getPlayer())) {
            dirtyBlocks.remove(blockPointer);
            return;
        }

        if (!getMember(event.getPlayer()).isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (!dirtyBlocks.contains(blockPointer)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can only break blocks placed by a player!");
            event.setCancelled(true);
            return;
        }

        dirtyBlocks.remove(blockPointer);
    }

    // Other fun events that make me cry
    private void handleDeathCommon(GameTeamMember member, Player player) {
        World world = player.getWorld();
        world.playEffect(player.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 0);
        for (ItemStack stack: player.getInventory()) {
            if (stack != null && stack.getType() == Material.DIAMOND)
                world.dropItem(player.getLocation(), stack.clone());
        }
        PlayerUtils.resetPlayer(player);
        player.setGameMode(GameMode.SPECTATOR);
        member.isAlive = false;
        if (member == diamondHolder)
            changeDiamondHolder(null);
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {  // TODO: Check friendly fire
        // Check that the damage was done to a playing player.
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Optional<GameTeamMember> member = getMember(player);
        if (!member.isPresent()) return;

        // Check that the player died.
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        // Announce the sad news (happens here so the spectator flare doesn't get added to the message)
        for (GameTeamMember otherMember: memberMap.values()) {
            Player otherPlayer = otherMember.getPlayer();
            otherPlayer.sendMessage(formatPlayerName(player) + ChatColor.GRAY + " died.");
            UiUtils.playSound(otherPlayer, Sound.BLAZE_DEATH);
        }

        // Set the player's state
        handleDeathCommon(member.get(), player);
        event.setDamage(0);
    }

    @EventHandler
    private void onPickup(PlayerPickupItemEvent event) {
        Optional<GameTeamMember> member = getMember(event.getPlayer());
        if (member.isPresent() && isSpawnedDiamond(event.getItem().getItemStack()))
            changeDiamondHolder(member.get());
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<GameTeamMember> member = getMember(player);
        if (!member.isPresent()) return;

        if (member.get().isAlive) {
            handleDeathCommon(member.get(), player);
        }
        removeMember(member.get());

        // Ensure that players are still in the game
        // TODO
    }

    // Lifecycle
    @Override
    public void onTick() {
        long timeUntilRoundEnd = TimestampUtils.getTimeUntil(roundEndTime, TimeUnits.Ms);
        if (timeUntilRoundEnd < 0) {
            startRound();
            return;
        }
        if (!spawnedDiamond && timeUntilRoundEnd < 1000 * 15) {
            spawnedDiamond = true;
            // TODO
        }

        // Poll for chase target change (fallback for the other events)
        // TODO: Polling is less than ideal for performance (O(36N) or O(N)). Once all possible exchange events are handled, this should be removed.


        // Display round time
        for (GameTeamMember member: memberMap.values()) {
            Player player = member.getPlayer();  // Runnables are executed before any player disconnection handling occurs

            player.setExp((float) (timeUntilRoundEnd % 1000) / 1000);
            player.setLevel((int) (timeUntilRoundEnd / 1000) + 1);
        }
    }

    @Override
    public void onPluginDisable() {
        resetWorld(true);
    }
}
