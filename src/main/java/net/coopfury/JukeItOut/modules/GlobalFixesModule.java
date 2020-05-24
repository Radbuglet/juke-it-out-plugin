package net.coopfury.JukeItOut.modules;

import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.spigot.ItemStackUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.InventoryHolder;

public class GlobalFixesModule extends PluginModule {
    public static boolean shouldDenyMapMakePrivilege(Player player) {
        return player.getGameMode() != GameMode.CREATIVE || !player.hasPermission(Constants.permission_map_making);
    }

    @EventHandler
    private void onCraftPreview(PrepareItemCraftEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (!(inventoryHolder instanceof Player) || shouldDenyMapMakePrivilege((Player) inventoryHolder)) {
            event.getInventory().setResult(ItemStackUtils.makeButtonItem(Material.BARRIER, Constants.message_crafting_disabled_item));
        }
    }

    @EventHandler
    private void onCraftConfirm(CraftItemEvent event) {
        HumanEntity clicker = event.getWhoClicked();
        if (!(clicker instanceof Player) || shouldDenyMapMakePrivilege((Player) clicker)) {
            event.setCancelled(true);
        }
    }

    // TODO: Disable everything!
}
