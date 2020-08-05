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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

// TODO: Should assume that, by contract, the config is valid.
public class GameStatePlaying implements GameState {
    private static final FireworkEffect diamondSpawnFwEffect = FireworkEffect.builder()
            .trail(false).flicker(false)
            .with(FireworkEffect.Type.BALL_LARGE)
            .withColor(Color.TEAL).build();

    private final List<GameTeam> teams = new ArrayList<>();
    private final Map<UUID, GameTeamMember> memberMap = new HashMap<>();
    private int roundId;
    private long roundEndTime;
    private boolean spawnedDiamond;
    private boolean chaseStarted;
    private final Set<BlockPointer> dirtyBlocks = new HashSet<>();

    public void startRound() {
        // Reset game state
        roundId++;
        roundEndTime = TimestampUtils.getTimeIn(TimeUnits.Secs, 30);
        spawnedDiamond = false;
        chaseStarted = false;

        // Reset characters
        for (GameTeam team: teams) {
            DyeColor color = team.configTeam.getWoolColor().orElse(DyeColor.WHITE);  // TODO

            for (GameTeamMember member: team.members) {
                // Reset game state
                member.isAlive = true;

                // Reset character
                Player player = member.getPlayer();
                PlayerUtils.resetPlayer(player);

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
                UiUtils.playTitle(player, String.format(ChatColor.RED + "Round %s", roundId), Constants.title_timings_important);
                UiUtils.playSound(player, Sound.ENDERDRAGON_HIT);
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

    public GameTeam makeTeam(ConfigTeam teamConfig) {
        GameTeam team = new GameTeam(teamConfig);
        teams.add(team);
        return team;
    }

    void registerMember(GameTeamMember member) {
        memberMap.put(member.playerUuid, member);
    }

    private void removeMember(GameTeamMember member) {
        memberMap.remove(member.playerUuid);
        member.team.unregisterMember(member);
    }

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

                Firework firework = world.spawn(diamondSpawn.get(), Firework.class);
                FireworkMeta fwMeta = firework.getFireworkMeta();
                fwMeta.addEffect(diamondSpawnFwEffect);
                fwMeta.setPower(0);
                firework.setFireworkMeta(fwMeta);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        firework.detonate();
                    }
                }.runTaskLater(Plugin.instance, 1);

                Item spawnedDiamond = world.dropItem(diamondSpawn.get(), new ItemStack(Material.DIAMOND));
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

        // Display round time
        for (GameTeamMember member: memberMap.values()) {
            Player player = member.getPlayer();  // Runnables are executed before any player disconnection handling occurs

            player.setExp((float) (timeUntilRoundEnd % 1000) / 1000);
            player.setLevel((int) (timeUntilRoundEnd / 1000) + 1);
        }
    }

    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent event) {
        if (!GlobalFixesModule.shouldDenyMapMakePrivilege(event.getPlayer())) {
            return;
        }

        if (!memberMap.containsKey(event.getPlayer().getUniqueId())) {
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

        if (!memberMap.containsKey(event.getPlayer().getUniqueId())) {
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

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        // Check that the damage was done to a playing player.
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        GameTeamMember member = memberMap.get(player.getUniqueId());
        if (member == null) return;

        // Check that the player died.
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        // Set the player's state
        player.setGameMode(GameMode.SPECTATOR);
        member.isAlive = false;

        // Announce the sad news
        // TODO

        event.setDamage(0);
    }

    @EventHandler
    private void onPickup(PlayerPickupItemEvent event) {
        GameTeamMember member = memberMap.get(event.getPlayer().getUniqueId());
        if (member == null || event.getItem().getItemStack().getType() != Material.DIAMOND) return;

        // Update player with halo
        // TODO

        // Update everyone else
        for (GameTeamMember otherMember: memberMap.values()) {  // TODO: Announce
            if (otherMember == member) continue;
            if (!chaseStarted)
                otherMember.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        }
        chaseStarted = true;
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameTeamMember member = memberMap.getOrDefault(player.getUniqueId(), null);
        if (member != null) {
            removeMember(member);
        }

        // Check that players are still in the game
        // TODO
    }

    @Override
    public void onPluginDisable() {
        resetWorld(true);
    }
}
