package net.coopfury.JukeItOut.helpers.spigot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class PlayerUtils {
    public static void resetPlayerEffects(Player player) {
        for (PotionEffect effect: player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public static void resetPlayer(Player player) {
        player.setExp(0);
        player.setLevel(0);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setLastDamageCause(null);
        player.setVelocity(new Vector(0, 0, 0));
        resetPlayerEffects(player);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
        player.setItemOnCursor(null);
        player.closeInventory();
    }

    /**
     * Sets the exact level of specific potion effect on the player.
     * This effect will last forever.
     * The level of the effect starts at zero, not one. If the value is negative, nothing will be applied.
     */
    public static void setEffectLevel(Player player, PotionEffectType type, int level) {
        player.removePotionEffect(type);
        if (level >= 0)
            player.addPotionEffect(new PotionEffect(type, 1000000, level));
    }
}
