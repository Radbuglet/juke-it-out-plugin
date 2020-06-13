package net.coopfury.JukeItOut.helpers.spigot;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public final class PlayerUtils {
    public static class LoadOutStack {
        public ItemStack stack;
        int amountNeeded;

        public LoadOutStack(ItemStack stack) {
            this.stack = stack;
            reset();
        }

        void reset() {
            amountNeeded = stack.getAmount();
        }
    }

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
}
