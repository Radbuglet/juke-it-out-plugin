package net.coopfury.JukeItOut.utils.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryGui {
    public interface ClickHandler {
        void handle(InventoryClickEvent event);
    }

    final Inventory inventory;
    private final Map<Integer, ClickHandler> guiItems = new HashMap<>();

    public InventoryGui(String name, int columns) {
        inventory = Bukkit.createInventory(null, columns * 9, name);
    }

    public void setItem(int slot, ItemStack itemStack) {
        ItemStack previousStack = inventory.getItem(slot);
        if (previousStack != null) guiItems.remove(slot);
        inventory.setItem(slot, itemStack);
    }

    public void setItem(int slot, ItemStack itemStack, ClickHandler handler) {
        setItem(slot, itemStack);
        if (itemStack != null)
            guiItems.put(slot, handler);
    }

    public int computeSlot(int horizontal, int vertical) {
        assert horizontal >= 0 && horizontal < 9;
        assert vertical >= 0 && vertical < inventory.getSize() / 9;
        return horizontal + vertical * 9;
    }

    public int getSize() {
        return inventory.getSize();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void handleClickEvent(InventoryClickEvent event) {
        assert event.getClickedInventory() == inventory;
        ClickHandler handler = guiItems.get(event.getSlot());
        event.setCancelled(true);
        if (handler != null)
            handler.handle(event);
    }
}
