package net.coopfury.JukeItOut.helpers.spigot;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class PlayerUtils {
    public static void resetPlayer(Player player) {
        player.setTotalExperience(0);
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SURVIVAL);
        player.setFireTicks(0);
        player.setLastDamageCause(null);
        player.setVelocity(new Vector(0, 0, 0));
        for (PotionEffect effect: player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.getInventory().clear();
        player.setItemOnCursor(null);
        player.closeInventory();
    }

    public static void setEffectLevel(Player player, PotionEffectType type, int level) {
        player.removePotionEffect(type);
        if (level >= 0)
            player.addPotionEffect(new PotionEffect(type, 1000000, level));
    }
}
