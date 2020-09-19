package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.java.TimeUnits;
import net.coopfury.JukeItOut.helpers.java.TimestampUtils;
import net.coopfury.JukeItOut.helpers.spigot.BlockPointer;
import net.coopfury.JukeItOut.helpers.spigot.ItemBuilder;
import net.coopfury.JukeItOut.helpers.spigot.PlayerUtils;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.GlobalFixesModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.GameState;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.GameTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.GameTeamMember;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.TeamManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Handles game exclusive events and serves as an entry point to game mechanics.
 */
public class GameStatePlaying implements GameState {
    private final Set<BlockPointer> dirtyBlocks = new HashSet<>();
    private final DiamondManager diamondManager = new DiamondManager();
    private final TeamManager teamManager = new TeamManager();

    // Round state
    private int roundId;
    private long roundEndTime;
    private boolean spawnedDiamond;

    // Public team management API
    public GameTeam makeTeam(ConfigTeam teamConfig) {
        return teamManager.makeTeam(teamConfig);
    }

    public void addPlayerToTeam(GameTeam team, Player player) {
        team.addMember(teamManager, player.getUniqueId());
    }

    public void formatAppendTeamName(StringBuilder builder, Player player) {
        teamManager.formatAppendTeamName(builder, player);
    }

    // Round management
    public void startRound() {
        // Reset game state
        roundId++;
        roundEndTime = TimestampUtils.getTimeIn(TimeUnits.Secs, 45);
        spawnedDiamond = false;
        diamondManager.resetRoundState(teamManager);

        // Reset characters
        for (GameTeam team: teamManager.getTeams()) {
            DyeColor color = team.configTeam.getWoolColor().orElse(DyeColor.WHITE);
            for (GameTeamMember member: team.members) {
                // Reset game state
                member.isAlive = true;

                // Reset character
                Player player = member.getPlayer();
                PlayerUtils.resetPlayer(player);
                player.setGameMode(GameMode.SURVIVAL);

                PlayerInventory inventory = player.getInventory();
                inventory.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                inventory.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                inventory.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .setLeatherArmorColor(color.getColor()).toItemStack());
                inventory.setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
                        .setLeatherArmorColor(color.getColor()).toItemStack());

                inventory.addItem(new ItemStack(Material.STONE_SWORD));
                inventory.addItem(new ItemStack(Material.IRON_PICKAXE));
                inventory.addItem(new ItemStack(Material.GOLDEN_APPLE, 4));
                inventory.addItem(new ItemBuilder(Material.STAINED_CLAY, 64).setDyeColor(color).toItemStack());

                player.teleport(team.configTeam.getSpawnLocation().orElse(null));
                UiUtils.playTitle(player,
                        String.format(ChatColor.RED + "Round %s", roundId),
                        isDefenseRound() ? ChatColor.DARK_RED + "Defense round" : null,
                        isDefenseRound() ? Constants.title_timings_long : Constants.title_timings_short);
                UiUtils.playSound(player, isDefenseRound() ? Sound.ENDERDRAGON_GROWL : Sound.ENDERDRAGON_HIT);
            }
            team.applyFriendlyEffects();
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

        for (EnderPearl pearl: world.get().getEntitiesByClass(EnderPearl.class)) {
            pearl.remove();
        }

        if (resetBlocks) {
            for (BlockPointer blockPointer : dirtyBlocks) {
                blockPointer.getBlock(world.get()).ifPresent(block -> block.setType(Material.AIR));
            }
            dirtyBlocks.clear();
        }
    }

    private boolean isDefenseRound() {
        return roundId % 5 == 0;
    }

    // Block handling
    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent event) {
        if (!GlobalFixesModule.shouldDenyMapMakePrivilege(event.getPlayer())) {
            return;
        }

        if (!teamManager.getMember(event.getPlayer()).isPresent()) {
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

        if (!teamManager.getMember(event.getPlayer()).isPresent()) {
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

    // Damage and death events
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
        if (member == diamondManager.getDiamondHolder())
            diamondManager.changeDiamondHolder(teamManager, null);
    }

    @EventHandler
    private void onDamageByOther(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player))
            return;

        Optional<GameTeam> damagerTeam = teamManager.getMemberTeam((Player) event.getDamager());
        if (!damagerTeam.isPresent()) return;

        if (teamManager.getMemberTeam((Player) event.getEntity()).orElse(null) == damagerTeam.orElse(null)) {
            event.setCancelled(true);
            event.getDamager().sendMessage(ChatColor.RED + "Do not try to damage your teammates!");
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)  // TODO: This should happen after WorldGuard damage cancel
    private void onDamage(EntityDamageEvent event) {
        // Check that the damage was done to a playing player.
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Optional<GameTeamMember> member = teamManager.getMember(player);
        if (!member.isPresent()) return;

        // Check that the player died.
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        // Announce the sad news (happens here so the spectator flare doesn't get added to the message)
        for (GameTeamMember otherMember: teamManager.getMembers()) {
            Player otherPlayer = otherMember.getPlayer();
            otherPlayer.sendMessage(teamManager.formatPlayerName(player) + ChatColor.GRAY + " died.");
            UiUtils.playSound(otherPlayer, Sound.BLAZE_DEATH);
        }

        // Set the player's state
        UiUtils.playTitle(player, ChatColor.BOLD.toString() + ChatColor.RED + "You died", Constants.title_timings_long);
        handleDeathCommon(member.get(), player);
        event.setDamage(0);
    }

    // Gameplay events
    @EventHandler
    private void onPickup(PlayerPickupItemEvent event) {
        Optional<GameTeamMember> member = teamManager.getMember(event.getPlayer());
        if (member.isPresent() && diamondManager.isSpawnedDiamond(event.getItem().getItemStack()))
            diamondManager.changeDiamondHolder(teamManager, member.get());
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        // Check if player is affected
        Player player = event.getPlayer();
        Optional<GameTeamMember> member = teamManager.getMember(player);
        if (!member.isPresent()) return;
        if (!member.get().isAlive) {
            event.setCancelled(true);
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Find owner
        GameTeam owningTeam = null;
        for (GameTeam team: teamManager.getTeams()) {
            Optional<Location> teamLocation;
            switch (clickedBlock.getType()) {
                case CHEST:
                    teamLocation = team.configTeam.getChestLocation();
                    break;
                case JUKEBOX:
                    teamLocation = team.configTeam.getJukeboxLocation();
                    break;
                default:
                    return;  // Never mind, the clicked block isn't a chest or a jukebox. Sorry for wasting your time, iterator.
            }

            if (teamLocation.isPresent() && teamLocation.get().getBlock().equals(clickedBlock)) {
                owningTeam = team;
                break;
            }
        }

        if (owningTeam == null) {
            player.sendMessage(ChatColor.RED + "You can't open the diamond reserves of empty teams.");
            player.playSound(event.getClickedBlock().getLocation(), Sound.DOOR_OPEN, 1, 1);
            event.setCancelled(true);
            return;
        }

        // Check defense round
        if (!isDefenseRound() && owningTeam != member.get().team) {
            player.sendMessage(ChatColor.RED + "You can only open your team's diamond reserves on non-defense rounds.");
            player.playSound(event.getClickedBlock().getLocation(), Sound.DOOR_OPEN, 1, 1);
            event.setCancelled(true);
            return;
        }

        // Handle normal logic (only does anything for jukebox)
        if (clickedBlock.getType() == Material.JUKEBOX) {
            event.setCancelled(true);
            owningTeam.openJukebox(player);  // openJukebox() plays the sound for us.
        }
    }

    // Join and leave events
    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<GameTeamMember> member = teamManager.getMember(player);
        if (!member.isPresent()) return;

        if (member.get().isAlive) {
            handleDeathCommon(member.get(), player);
        }
        teamManager.removeMember(member.get());

        // End the game if everyone left
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
        if (!spawnedDiamond && timeUntilRoundEnd < 1000 * 25) {
            spawnedDiamond = true;
            diamondManager.spawnDiamond(teamManager);
        }

        // Poll for chase target change (fallback for the other events)
        diamondManager.pollDiamondHolderChange(teamManager);

        // Display round time
        for (GameTeamMember member: teamManager.getMembers()) {
            Player player = member.getPlayer();  // Runnables are executed before any player disconnection handling occurs

            player.setExp((float) (timeUntilRoundEnd % 1000) / 1000);
            player.setLevel((int) (timeUntilRoundEnd / 1000) + 1);
        }

        // Apply offensive effects
        for (GameTeam team: teamManager.getTeams()) {
            team.applyOffensiveEffects(teamManager);
        }
    }

    @Override
    public void onPluginDisable() {
        resetWorld(true);
    }

    @Override
    public void onStateDisable() {
        for (GameTeam team: teamManager.getTeams()) {
            team.onGameStateChange();
        }
    }
}
