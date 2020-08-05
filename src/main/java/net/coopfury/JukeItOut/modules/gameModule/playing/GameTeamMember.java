package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.spigot.ItemBuilder;
import net.coopfury.JukeItOut.helpers.spigot.PlayerUtils;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class GameTeamMember {
    public final GameTeam team;
    public final UUID playerUuid;
    public boolean isAlive;

    public GameTeamMember(GameTeam team, UUID playerUuid) {
        this.team = team;
        this.playerUuid = playerUuid;
    }

    void resetCharacter(int roundId, DyeColor teamColor) {
        // Reset game state
        isAlive = true;

        // Reset character
        Player player = getPlayer();
        PlayerUtils.resetPlayer(player);

        PlayerInventory inventory = player.getInventory();
        inventory.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        inventory.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        inventory.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .setLeatherArmorColor(teamColor.getColor()).toItemStack());
        inventory.setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
                .setLeatherArmorColor(teamColor.getColor()).toItemStack());

        inventory.addItem(new ItemStack(Material.STONE_SWORD));
        inventory.addItem(new ItemStack(Material.IRON_PICKAXE));
        inventory.addItem(new ItemStack(Material.GOLDEN_APPLE, 4));
        inventory.addItem(new ItemBuilder(Material.STAINED_CLAY, 64).setDyeColor(teamColor).toItemStack());

        player.teleport(team.configTeam.getSpawnLocation().orElse(null));
        UiUtils.playTitle(player, String.format(ChatColor.RED + "Round %s", roundId), Constants.title_timings_important);
        UiUtils.playSound(player, Sound.ENDERDRAGON_HIT);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUuid);
    }
}
