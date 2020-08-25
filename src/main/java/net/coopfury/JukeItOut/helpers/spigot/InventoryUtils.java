package net.coopfury.JukeItOut.helpers.spigot;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class InventoryUtils {
    public static boolean tryPurchase(Inventory inventory, Function<ItemStack, Boolean> eligibilityChecker, int quantity) {
        if (quantity == 0) return true;

        List<ItemStack> consumedStacks = new ArrayList<>(Math.min(inventory.getSize(), quantity - 1));
        for (ItemStack stack : inventory) {
            if (stack == null || !eligibilityChecker.apply(stack)) continue;

            // See if the purchase is over
            if (quantity <= stack.getAmount()) {
                // Partially consume this stack
                stack.setAmount(stack.getAmount() - quantity);

                // Consume the other stacks
                for (ItemStack consumedStack: consumedStacks) {
                    consumedStack.setAmount(0);
                }

                return true;
            }

            // Fully consume the stack
            quantity -= stack.getAmount();
            consumedStacks.add(stack);
        }

        return false;
    }
}
