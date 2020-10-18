package net.coopfury.JukeItOut.helpers.spigot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class PlayerUtils {
    public static void resetEffects(Player player) {
        for (PotionEffect effect: player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public static void resetInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setHelmet(null);
        inventory.setChestplate(null);
        inventory.setLeggings(null);
        inventory.setBoots(null);
        player.setItemOnCursor(null);
        player.closeInventory();
    }

    public static void resetAll(Player player) {
        player.setTotalExperience(0);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setLastDamageCause(null);
        player.setVelocity(new Vector(0, 0, 0));
        resetEffects(player);
        resetInventory(player);
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

    public static boolean tryPurchase(Inventory inventory, Function<ItemStack, Boolean> eligibilityChecker, int quantity) {
        if (quantity == 0) return true;

        List<Integer> consumedStacks = new ArrayList<>(Math.min(inventory.getSize(), quantity - 1));
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack == null || !eligibilityChecker.apply(stack)) continue;

            // See if the purchase is over
            if (quantity <= stack.getAmount()) {
                // Partially consume this stack
                if (stack.getAmount() != quantity) {
                    stack.setAmount(stack.getAmount() - quantity);
                } else {
                    inventory.clear(slot);
                }

                // Consume the other stacks
                for (int consumedSlot: consumedStacks) {
                    inventory.clear(consumedSlot);
                }

                return true;
            }

            // Fully consume the stack
            quantity -= stack.getAmount();
            consumedStacks.add(slot);
        }

        return false;
    }
}
