package net.coopfury.JukeItOut.modules;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.java.signal.SignalPriority;
import net.coopfury.JukeItOut.helpers.spigot.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GlobalFixes implements Listener {
    private final Set<UUID> devModeUsers = new HashSet<>();

    public GlobalFixes() {
        Plugin.instance.onEnable.connect(plugin -> plugin.registerListener(this), SignalPriority.Low);
    }

    public boolean hasDevMode(UUID uuid) {
        return devModeUsers.contains(uuid);
    }

    @EventHandler
    private void onCraftPreview(PrepareItemCraftEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (!(inventoryHolder instanceof Player) || !hasDevMode(((Player) inventoryHolder).getUniqueId())) {
            event.getInventory().setResult(new ItemBuilder(Material.BARRIER)
                    .setName(ChatColor.RED + "Crafting disabled")
                    .toItemStack());
        }
    }

    @EventHandler
    private void onCraftConfirm(CraftItemEvent event) {
        HumanEntity clicker = event.getWhoClicked();
        if (!hasDevMode(clicker.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!hasDevMode(event.getPlayer().getUniqueId()) &&
                event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        devModeUsers.remove(event.getPlayer().getUniqueId());
    }
}
