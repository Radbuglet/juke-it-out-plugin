package net.coopfury.JukeItOut.state.game.playing.managers;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.state.game.playing.GameStatePlaying;
import net.coopfury.JukeItOut.state.game.playing.teams.GameMember;
import net.coopfury.JukeItOut.state.game.playing.teams.GameTeam;
import net.coopfury.JukeItOut.utils.java.signal.EventSignal;
import net.coopfury.JukeItOut.utils.spigot.ItemBuilder;
import net.coopfury.JukeItOut.utils.spigot.UiUtils;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Optional;

public class DiamondManager {
    // Config
    private static final String STOLEN_DIAMOND_NAME = ChatColor.BLUE + "Stolen Diamond";
    private static final FireworkEffect SPAWN_FIREWORK_EFFECT = FireworkEffect.builder()
            .trail(false).flicker(false)
            .with(FireworkEffect.Type.BALL_LARGE)
            .withColor(Color.TEAL)
            .build();

    // Signals
    public final EventSignal<Optional<GameMember>> onDiamondHolderChange = new EventSignal<>();

    // Properties
    private final GameStatePlaying root;
    private boolean chaseStarted;
    private GameMember diamondHolder;

    public DiamondManager(GameStatePlaying root) {
        this.root = root;
    }

    // Diamond detection methods
    public boolean isSpawnedDiamond(ItemStack stack) {
        return stack != null && stack.getType() == Material.DIAMOND
                && STOLEN_DIAMOND_NAME.equals(stack.getItemMeta().getDisplayName());
    }

    private boolean hasSpawnedDiamond(Inventory inventory) {
        for (ItemStack stack : inventory) {
            if (isSpawnedDiamond(stack))
                return true;
        }
        return false;
    }

    private boolean hasSpawnedDiamond(Player player) {
        return hasSpawnedDiamond(player.getInventory());
    }

    // Event handling
    public Optional<Location> getDiamondSpawnLocation() {
        return Plugin.instance.config.getRoot().getDiamondSpawn();
    }

    public void spawnDiamond() {
        // Spawn diamond
        Optional<Location> diamondSpawn = getDiamondSpawnLocation();
        if (diamondSpawn.isPresent()) {
            World world = diamondSpawn.get().getWorld();

            // Spawn firework effect
            Firework firework = world.spawn(diamondSpawn.get(), Firework.class);
            FireworkMeta fwMeta = firework.getFireworkMeta();
            fwMeta.addEffect(SPAWN_FIREWORK_EFFECT);
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
                    .setName(STOLEN_DIAMOND_NAME)
                    .toItemStack());
            spawnedDiamond.setVelocity(new Vector(0, .5, 0));
        } else {
            Plugin.instance.getLogger().warning("Failed to spawn diamond: no spawn location set.");
        }

        // Announce spawn
        for (GameMember member: root.teamManager.getMembers()) {
            if (member.isAlive) {
                Player player = member.getPlayer();
                UiUtils.playTitle(player, ChatColor.LIGHT_PURPLE + "Diamond Spawned", Constants.title_timings_short);
                UiUtils.playSound(player, Sound.LEVEL_UP);
            }
        }
    }

    public void resetRoundState() {
        // Reset state
        chaseStarted = false;
        diamondHolder = null;

        // Unmark diamonds as stolen
        for (GameTeam team: root.teamManager.getTeams()) {
            Optional<Chest> chest = team.getTeamChest();
            if (!chest.isPresent()) continue;
            Inventory chestInventory = chest.get().getBlockInventory();
            for (ItemStack stack: chestInventory) {
                if (!isSpawnedDiamond(stack)) continue;
                chestInventory.remove(stack);
                chestInventory.addItem(new ItemStack(Material.DIAMOND, stack.getAmount()));
                return;  // There's only ever one stolen diamond, so we can get away with early returns and stack removal.
            }
        }
    }

    // Diamond holder system
    public GameMember getDiamondHolder() {
        return diamondHolder;
    }

    public void setDiamondHolder(GameMember member) {
        if (member == diamondHolder) return;
        diamondHolder = member;
        onDiamondHolderChange.fire(Optional.ofNullable(member));

        if (diamondHolder != null) {
            assert member != null;
            Player player = member.getPlayer();

            // Update everyone else
            for (GameMember otherMember : root.teamManager.getMembers()) {
                // Send message
                Player otherPlayer = otherMember.getPlayer();
                UiUtils.playSound(otherPlayer, Sound.GHAST_SCREAM2);
                otherPlayer.sendMessage(root.teamManager.formatPlayerName(player) + ChatColor.AQUA + " picked up the diamond!");

                // Give items
                if (!chaseStarted && otherMember.isAlive && otherMember != member)
                    otherMember.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            }
            chaseStarted = true;
        }
    }

    private void pollDiamondHolder() {
        if (diamondHolder != null && diamondHolder.isAlive && hasSpawnedDiamond(diamondHolder.getPlayer()))
            return;  // The current diamond holder still has the diamond. No one else should have the diamond.

        // Since the current holder doesn't have the diamond or doesn't exist, poll for a new holder.
        for (GameMember newHolder: root.teamManager.getMembers()) {
            if (newHolder.isAlive && hasSpawnedDiamond(newHolder.getPlayer())) {
                setDiamondHolder(newHolder);  // We found the new diamond holder!
                return;
            }
        }

        // We failed to find anyone holding the diamond.
        setDiamondHolder(null);
    }

    public void tick() {
        pollDiamondHolder();

        if (diamondHolder != null) {
            Player player = diamondHolder.getPlayer();
            player.getWorld().playEffect(player.getLocation().add(new Vector(0, 0.25, 0)), Effect.SPELL, null);
        }
    }
}
