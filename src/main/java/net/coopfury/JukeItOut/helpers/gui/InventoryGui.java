package net.coopfury.JukeItOut.helpers.gui;

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
    private final Map<ItemStack, ClickHandler> guiItems = new HashMap<>();

    public InventoryGui(String name, int columns) {
        inventory = Bukkit.createInventory(null, columns * 9, name);
    }

    public void setItemRaw(int slot, ItemStack itemStack) {
        ItemStack previousStack = inventory.getItem(slot);
        if (previousStack != null) guiItems.remove(previousStack);
        inventory.setItem(slot, itemStack);
    }

    public void setItem(int slot, ItemStack itemStack, ClickHandler handler) {
        setItemRaw(slot, itemStack);
        if (itemStack != null)
            guiItems.put(itemStack, handler);
    }

    public void setItem(int slot, ItemStack itemStack) {
        setItem(slot, itemStack, event -> event.setCancelled(true));
    }

    public int computeSlot(int row, int column) {
        assert row >= 0 && row < 9;
        assert column >= 0 && column < inventory.getSize() / 9;
        return row * 9 + column;
    }

    public int getSize() {
        return inventory.getSize();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void handleClickEvent(InventoryClickEvent event) {
        assert event.getClickedInventory() == inventory;
        ClickHandler handler = guiItems.get(event.getCurrentItem());
        handler.handle(event);
    }
}