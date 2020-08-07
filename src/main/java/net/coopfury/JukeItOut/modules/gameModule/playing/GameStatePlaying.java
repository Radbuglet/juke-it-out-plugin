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
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

// TODO: In the future, this implementation should be split up into multiple classes (e.g. MemberManager, RoundManager, MainManager (this))
public class GameStatePlaying implements GameState {
    private static final FireworkEffect diamondSpawnFwEffect = FireworkEffect.builder()
            .trail(false).flicker(false)
            .with(FireworkEffect.Type.BALL_LARGE)
            .withColor(Color.TEAL).build();
    private static final String stolen_diamond_name = ChatColor.BLUE + "Stolen Diamond";

    private final List<GameTeam> teams = new ArrayList<>();
    private final Map<UUID, GameTeamMember> memberMap = new HashMap<>();
    private final Set<BlockPointer> dirtyBlocks = new HashSet<>();

    // Round state
    private int roundId;
    private long roundEndTime;
    private boolean spawnedDiamond;
    private boolean chaseStarted;
    private GameTeamMember diamondHolder;

    // Round management
    public void startRound() {
        // Reset game state
        roundId++;
        roundEndTime = TimestampUtils.getTimeIn(TimeUnits.Secs, 30);
        spawnedDiamond = false;
        chaseStarted = false;
        diamondHolder = null;

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
    void registerMember(GameTeamMember member) {
        memberMap.put(member.playerUuid, member);
    }

    private void removeMember(GameTeamMember member) {
        memberMap.remove(member.playerUuid);
        member.team.unregisterMember(member);
    }

    public GameTeam makeTeam(ConfigTeam teamConfig) {
        GameTeam team = new GameTeam(teamConfig);
        teams.add(team);
        return team;
    }

    private Optional<GameTeamMember> getMember(Player player) {
        return Optional.ofNullable(memberMap.get(player.getUniqueId()));
    }

    public void formatAppendTeamName(StringBuilder builder, Player player) {
        Optional<GameTeamMember> member = getMember(player);
        if (!member.isPresent() || !member.get().isAlive) {
            builder.append(ChatColor.GRAY).append("[SPECTATOR] ");
        }
        member.ifPresent(gameTeamMember -> builder.append(gameTeamMember.team.getTextColor().orElse(ChatColor.WHITE))
                .append("[")
                .append(gameTeamMember.team.configTeam.getName().orElse("UNNAMED").toUpperCase())
                .append("] "));
    }

    public String formatPlayerName(Player player) {
        StringBuilder builder = new StringBuilder();
        formatAppendTeamName(builder, player);
        builder.append(UiUtils.formatVaultName(player));
        return builder.toString();
    }

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
    private boolean isSpawnedDiamond(ItemStack stack) {
        return stack != null && stack.getType() == Material.DIAMOND && stack.getItemMeta().getDisplayName().equals(stolen_diamond_name);
    }

    private boolean hasSpawnedDiamond(Player player) {
        for (ItemStack stack : player.getInventory()) {
            if (isSpawnedDiamond(stack))
                return true;
        }
        return false;
    }

    private void changeDiamondHolder(GameTeamMember member) {
        if (member == diamondHolder) return;
        diamondHolder = member;

        if (diamondHolder != null) {
            Player player = member.getPlayer();

            // Update player with halo
            // TODO

            // Update everyone else
            for (GameTeamMember otherMember : memberMap.values()) {
                // Send message
                Player otherPlayer = otherMember.getPlayer();
                UiUtils.playSound(otherPlayer, Sound.GHAST_SCREAM2);
                otherPlayer.sendMessage(formatPlayerName(player) + ChatColor.AQUA + " picked up the diamond!");

                // Give items
                if (!chaseStarted && otherMember.isAlive && otherMember != member)
                    otherMember.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            }
            chaseStarted = true;
        }
    }

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
            // Spawn diamond
            Optional<Location> diamondSpawn = Plugin.getModule(ConfigLoadingModule.class).root.getDiamondSpawn();
            if (diamondSpawn.isPresent()) {
                World world = diamondSpawn.get().getWorld();

                // Spawn firework effect
                Firework firework = world.spawn(diamondSpawn.get(), Firework.class);
                FireworkMeta fwMeta = firework.getFireworkMeta();
                fwMeta.addEffect(diamondSpawnFwEffect);
                fwMeta.setPower(0);
                firework.setFireworkMeta(fwMeta);
                new BukkitRunnable() {  // Runnable used here because it seems that fireworks can't get detonated until CraftBukkit has processed them for at least one tick.
                    @Override
                    public void run() {
                        firework.detonate();
                    }
                }.runTaskLater(Plugin.instance, 1);

                // Spawn item
                Item spawnedDiamond = world.dropItem(diamondSpawn.get(), new ItemBuilder(Material.DIAMOND)
                        .setName(stolen_diamond_name)
                        .toItemStack());
                spawnedDiamond.setVelocity(new Vector(0, .5, 0));
            } else {
                Plugin.instance.getLogger().warning("Failed to spawn diamond: no spawn location set.");
            }

            // Announce spawn
            for (GameTeamMember member: memberMap.values()) {
                if (member.isAlive) {
                    Player player = member.getPlayer();
                    UiUtils.playTitle(player, ChatColor.LIGHT_PURPLE + "Diamond Spawned", Constants.title_timings_important);
                    UiUtils.playSound(player, Sound.LEVEL_UP);
                }
            }
        }

        // Poll for chase target change (fallback for the other events)
        // TODO: Polling is less than ideal for performance (O(36N) or O(N)). Once all possible exchange events are handled, this should be removed.
        if (diamondHolder == null || !diamondHolder.isAlive || !hasSpawnedDiamond(diamondHolder.getPlayer())) {
            boolean foundHolder = false;
            // Since the current holder no longer has the diamond, check the other players.
            for (GameTeamMember newHolder: memberMap.values()) {
                if (newHolder.isAlive && hasSpawnedDiamond(newHolder.getPlayer())) {
                    changeDiamondHolder(newHolder);
                    foundHolder = true;
                    break;
                }
            }

            if (!foundHolder)
                changeDiamondHolder(null);
        }

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
