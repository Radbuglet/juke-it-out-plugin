package net.coopfury.JukeItOut.helpers.spigot;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class InventoryUtils {
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
