package net.coopfury.JukeItOut.utils.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class InventoryActionDelegator implements Listener {
    private final Map<Inventory, InventoryGui> menus = new HashMap<>();

    public void bind(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        InventoryGui gui = menus.get(event.getClickedInventory());
        if (gui == null) return;
        gui.handleClickEvent(event);
    }

    public void registerMenu(InventoryGui gui) {
        menus.put(gui.inventory, gui);
    }

    public void unregisterMenu(InventoryGui gui) {
        menus.remove(gui.inventory);
    }
}
