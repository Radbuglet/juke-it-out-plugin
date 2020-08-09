package net.coopfury.JukeItOut.helpers.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The ClickableStackManager provides a system for binding click actions to any item in an inventory to a specific player.
 */
public class ClickableStackManager implements Listener {
    public interface ClickHandler {
        default void handle(InventoryClickEvent event) {}
    }

    public static class RegisteredPlayer {
        public final Map<ItemStack, ClickHandler> stacks = new HashMap<>();
        public final Inventory inventory;

        public RegisteredPlayer(Inventory inventory) {
            this.inventory = inventory;
        }
    }

    private final Map<UUID, RegisteredPlayer> playerClickHandlers = new HashMap<>();

    // Registration
    public ClickableStackManager(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this::cleanUpHandlers, 0, 20);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // API
    public void bindAction(Player player, ItemStack stack, ClickHandler handler) {
        Inventory guiInventory = player.getOpenInventory().getTopInventory();
        assert guiInventory != null;
        assert guiInventory.contains(stack);
        playerClickHandlers.computeIfAbsent(player.getUniqueId(), uuid -> new RegisteredPlayer(guiInventory))
            .stacks.put(stack, handler);
    }

    // Handlers
    private void cleanUpHandlers() {
        playerClickHandlers.entrySet().removeIf(playerEntry -> {
            // Purge missing players/inventories
            Player player = Bukkit.getPlayer(playerEntry.getKey());
            Inventory inventory = playerEntry.getValue().inventory;
            if (player == null || inventory != player.getOpenInventory().getTopInventory()) {
                return true;
            }

            // Purge missing stacks
            playerEntry.getValue().stacks.keySet().removeIf(stack -> !inventory.contains(stack));
            return false;
        });
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        RegisteredPlayer registeredPlayer = playerClickHandlers.get(event.getWhoClicked().getUniqueId());
        if (registeredPlayer == null) return;
        ClickHandler handler = registeredPlayer.stacks.get(event.getCurrentItem());
        if (handler != null)
            handler.handle(event);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        playerClickHandlers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        playerClickHandlers.remove(event.getPlayer().getUniqueId());
    }
}
