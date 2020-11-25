package net.coopfury.JukeItOut.state.game.playing.managers;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.state.game.playing.GameStatePlaying;
import net.coopfury.JukeItOut.state.game.playing.teams.GameMember;
import net.coopfury.JukeItOut.state.game.playing.teams.GameTeam;
import net.coopfury.JukeItOut.utils.spigot.ItemBuilder;
import net.coopfury.JukeItOut.utils.spigot.PlayerUtils;
import net.coopfury.JukeItOut.utils.spigot.UiUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;

public class CombatManager {
    private final GameStatePlaying root;

    public CombatManager(GameStatePlaying root) {
        this.root = root;
    }

    public void startRound() {
        for (GameTeam team: root.teamManager.getTeams()) {
            DyeColor color = team.configTeam.getWoolColor().orElse(DyeColor.WHITE);
            for (GameMember member: team.getMembers()) {
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
                        String.format(ChatColor.RED + "Round %s", root.roundManager.getRoundNumber()),
                        root.roundManager.isDefenseRound() ? ChatColor.DARK_RED + "Defense round" : null,
                        root.roundManager.isDefenseRound() ? Constants.title_timings_long : Constants.title_timings_short);
                UiUtils.playSound(player, root.roundManager.isDefenseRound() ? Sound.ENDERDRAGON_GROWL : Sound.ENDERDRAGON_HIT);
            }

            team.applyFriendlyEffects();
        }
    }

    public void memberKilled(GameMember member, Player player) {
        World world = player.getWorld();
        world.playEffect(player.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 0);
        for (ItemStack stack: player.getInventory()) {
            if (stack != null && stack.getType() == Material.DIAMOND)
                world.dropItem(player.getLocation(), stack.clone());
        }
        PlayerUtils.resetAll(player);
        player.setGameMode(GameMode.SPECTATOR);
        member.isAlive = false;
        if (member == root.diamondManager.getDiamondHolder())
            root.diamondManager.setDiamondHolder(null);
    }

    public void onEntityAttacked(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player))
            return;

        Optional<GameTeam> damagerTeam = root.teamManager.getMemberTeam(event.getDamager().getUniqueId());
        if (!damagerTeam.isPresent()) return;


        if (root.teamManager.getMemberTeam(event.getEntity().getUniqueId()).orElse(null) == damagerTeam.get()) {
            event.setCancelled(true);
            event.getDamager().sendMessage(ChatColor.RED + "Do not try to damage your teammates!");
        }
    }

    public void onEntityDamaged(EntityDamageEvent event) {
        // Check that the damage was done to a playing player.
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Optional<GameMember> damaged = root.teamManager.getMember(player.getUniqueId());
        if (!damaged.isPresent()) return;

        // Prevent fall damage
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }

        // Check that the player died.
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        // Find killer
        EntityDamageEvent lastDamage = event.getEntity().getLastDamageCause();
        Player killer = null;
        if (lastDamage instanceof EntityDamageByEntityEvent) {
            Entity killerEnt = ((EntityDamageByEntityEvent) lastDamage).getDamager();
            if (killerEnt instanceof Player) {
                killer = (Player) killerEnt;
            }
        }

        // Heal the killer
        if (killer != null) {
            PlayerUtils.heal(killer, 5);
        }

        // Announce the sad news (happens here so the spectator flare doesn't get added to the message)
        String message = killer == null ?
                root.teamManager.formatPlayerName(player) + ChatColor.GRAY + " died." :
                root.teamManager.formatPlayerName(player) + ChatColor.GRAY + " was killed by " + root.teamManager.formatPlayerName(killer) + ".";

        for (GameMember otherMember: root.teamManager.getMembers()) {
            Player otherPlayer = otherMember.getPlayer();
            otherPlayer.sendMessage(message);
            UiUtils.playSound(otherPlayer, Sound.BLAZE_DEATH);
        }

        // Set the player's state
        UiUtils.playTitle(player, ChatColor.BOLD.toString() + ChatColor.RED + "You died", Constants.title_timings_long);
        memberKilled(damaged.get(), player);
        event.setDamage(0);
    }
}
