package net.coopfury.JukeItOut.modules;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.java.signal.SignalPriority;
import net.coopfury.JukeItOut.helpers.spigot.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

public class GlobalProtect implements Listener {
    public GlobalProtect() {
        Plugin.instance.onEnable.connect(plugin -> plugin.registerListener(this), SignalPriority.Low);
    }

    public boolean shouldAffect(HumanEntity entity) {
        return !entity.hasPermission(Constants.permission_map_making) || entity.getGameMode() != GameMode.CREATIVE;
    }

    @EventHandler
    private void onCraftPreview(PrepareItemCraftEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (!(inventoryHolder instanceof HumanEntity) || shouldAffect((HumanEntity) inventoryHolder)) {
            event.getInventory().setResult(new ItemBuilder(Material.BARRIER)
                    .setName(ChatColor.RED + "Crafting disabled")
                    .toItemStack());
        }
    }

    @EventHandler
    private void onCraftConfirm(CraftItemEvent event) {
        HumanEntity clicker = event.getWhoClicked();
        if (shouldAffect(clicker)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (shouldAffect(event.getPlayer()) &&
                event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }
}
