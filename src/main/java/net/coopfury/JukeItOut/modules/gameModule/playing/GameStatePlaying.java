package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.game.MemberPair;
import net.coopfury.JukeItOut.helpers.java.TimeUnits;
import net.coopfury.JukeItOut.helpers.java.TimestampUtils;
import net.coopfury.JukeItOut.helpers.spigot.*;
import net.coopfury.JukeItOut.modules.GlobalFixesModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.GameState;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.GameTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.GameTeamMember;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.TeamManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

/**
 * Handles game exclusive events and serves as an entry point to game mechanics.
 */
public class GameStatePlaying implements GameState {
    // Services
    private final DiamondSpawner diamondSpawner = new DiamondSpawner();
    private final TeamManager teamManager = new TeamManager();

    // Resources
    private final Set<BlockPointer> dirtyBlocks = new HashSet<>();
    private final Objective guiObjective;
    private final BukkitTask taskPollTimer;

    // Round state
    private int roundId;
    private long roundEndTime;
    private boolean spawnedDiamond;

    // Lifecycle
    public GameStatePlaying() {
        // Make scoreboard GUI
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        guiObjective = ScoreboardUtils.obtainObjective(scoreboard, "cf_diamonds_gui", ChatColor.GOLD + "Team Diamonds", "dummy");
        guiObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Create the poll timer
        taskPollTimer = new BukkitRunnable() {
            @Override
            public void run() {
                for (GameTeam team: teamManager.getTeams()) {
                    team.updateDiamondScoreGui();
                }
            }
        }.runTaskTimer(Plugin.instance, 0, 5);
    }

    private void cleanupResources() {
        guiObjective.unregister();
        taskPollTimer.cancel();
    }

    @Override
    public void onPluginDisable() {
        resetWorld(true);
        cleanupResources();
    }

    @Override
    public void onStateDisable() {
        resetWorld(true);
        cleanupResources();
        for (GameTeam team: teamManager.getTeams()) {
            team.onStateDisable();
        }
    }

    // Public team management API
    public void makeTeam(ConfigTeam teamConfig) {
        GameTeam team = new GameTeam(guiObjective, teamConfig);
        teamManager.addTeam(team);
    }

    public void addPlayerToGame(Player player) {
        teamManager.addPlayerToGame(player.getUniqueId());
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
        diamondSpawner.resetRoundState(teamManager);

        // Reset characters
        for (GameTeam team: teamManager.getTeams()) {
            DyeColor color = team.configTeam.getWoolColor().orElse(DyeColor.WHITE);
            for (GameTeamMember member: team.getMembers()) {
                // Reset game state
                member.isAlive = true;

                // Reset character
                Player player = member.getPlayer();
                PlayerUtils.resetAll(player);
                player.setGameMode(GameMode.SURVIVAL);

                PlayerInventory inventory = player.getInventory();
                inventory.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                inventory.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                inventory.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .setLeatherArmorColor(color.getColor()).toItemStack());
                inventory.setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
                        .setLeatherArmorColor(color.getColor()).toItemStack());

                inventory.addItem(new ItemStack(Material.STONE_SWORD));
                inventory.addItem(new ItemBuilder(Material.IRON_PICKAXE)
                        .addEnchant(Enchantment.DIG_SPEED, 2).toItemStack());
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
            for (GameTeam team: teamManager.getTeams()) {
                Optional<Location> chestLocation = team.configTeam.getChestLocation();
                if (!chestLocation.isPresent()) continue;
                BlockState blockState = chestLocation.get().getBlock().getState();
                if (!(blockState instanceof Chest)) continue;
                ((Chest) blockState).getBlockInventory().clear();
                blockState.update(true);
            }
        }
    }

    private boolean isDefenseRound() {
        return roundId % 5 == 0;
    }

    // Tick handling
    @Override
    public void onTick() {
        long timeUntilRoundEnd = TimestampUtils.getTimeUntil(roundEndTime, TimeUnits.Ms);
        if (timeUntilRoundEnd < 0) {
            startRound();
            return;
        }
        if (!spawnedDiamond && timeUntilRoundEnd < 1000 * 25) {
            spawnedDiamond = true;
            diamondSpawner.spawnDiamond(teamManager);
        }

        // Poll for chase target change (fallback for the other events)
        diamondSpawner.pollDiamondHolderChange(teamManager);

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

    // Block handling
    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent event) {
        if (!GlobalFixesModule.shouldDenyMapMakePrivilege(event.getPlayer())) {
            return;
        }

        if (!teamManager.getMember(event.getPlayer().getUniqueId()).isPresent()) {
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

        if (!teamManager.getMember(event.getPlayer().getUniqueId()).isPresent()) {
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
        PlayerUtils.resetAll(player);
        player.setGameMode(GameMode.SPECTATOR);
        member.isAlive = false;
        if (member == diamondSpawner.getDiamondHolder())
            diamondSpawner.changeDiamondHolder(teamManager, null);
    }

    @EventHandler
    private void onDamageByOther(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player))
            return;

        Optional<GameTeam> damagerTeam = teamManager.getMemberTeam(event.getDamager().getUniqueId());
        if (!damagerTeam.isPresent()) return;

        if (teamManager.getMemberTeam(event.getEntity().getUniqueId()).orElse(null) == damagerTeam.get()) {
            event.setCancelled(true);
            event.getDamager().sendMessage(ChatColor.RED + "Do not try to damage your teammates!");
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    private void onDamage(EntityDamageEvent event) {
        // Check that the damage was done to a playing player.
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Optional<GameTeamMember> member = teamManager.getMember(player.getUniqueId());
        if (!member.isPresent()) return;

        // Prevent fall damage
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }

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
        Optional<GameTeamMember> member = teamManager.getMember(event.getPlayer().getUniqueId());
        if (member.isPresent() && diamondSpawner.isSpawnedDiamond(event.getItem().getItemStack()))
            diamondSpawner.changeDiamondHolder(teamManager, member.get());
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        // Check if player is affected
        Player player = event.getPlayer();

        Optional<MemberPair<GameTeam, GameTeamMember>> memberPair =
                teamManager.getMemberPair(player.getUniqueId());

        if (!memberPair.isPresent()) return;
        if (!memberPair.get().member.isAlive) {
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
        if (!isDefenseRound() && owningTeam != memberPair.get().team) {
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
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Add player back into game
        Optional<MemberPair<GameTeam, GameTeamMember>> memberPair = teamManager.addPlayerToGame(player.getUniqueId());
        if (!memberPair.isPresent()) {
            player.sendMessage(ChatColor.RED + "Failed to add you into a team (game not configured correctly)");
            return;
        }

        // Setup player
        memberPair.get().member.isAlive = false;
        PlayerUtils.resetAll(player);
        player.setGameMode(GameMode.SPECTATOR);
        diamondSpawner.getDiamondSpawnLocation().ifPresent(player::teleport);

        // Explain what's happening
        UiUtils.playSound(player, Sound.NOTE_PLING);
        UiUtils.playTitle(player, ChatColor.GREEN + "Please wait", ChatColor.GRAY + "You will join the game next round.", Constants.title_timings_long);
        player.sendMessage(ChatColor.GREEN + "You are currently a spectator until the next round starts!");
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Handle member removal
        Optional<GameTeamMember> member = teamManager.getMember(player.getUniqueId());
        if (!member.isPresent()) return;

        if (member.get().isAlive) {
            handleDeathCommon(member.get(), player);
        }
        teamManager.removePlayerFromGame(player.getUniqueId());

        // End the game if everyone left
        // TODO
    }
}
