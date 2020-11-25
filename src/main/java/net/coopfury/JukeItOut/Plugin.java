package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.listeners.GameEvents;
import net.coopfury.JukeItOut.utils.gui.InventoryActionDelegator;
import net.coopfury.JukeItOut.utils.java.signal.EventSignal;
import net.coopfury.JukeItOut.listeners.GlobalProtect;
import net.coopfury.JukeItOut.commands.CommandRegistrar;
import net.coopfury.JukeItOut.state.config.ConfigLoading;
import net.coopfury.JukeItOut.state.game.GameManager;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class Plugin extends JavaPlugin {
    // Core
    public static Plugin instance;
    public final EventSignal<Plugin> onEnable = new EventSignal<>();
    public final EventSignal<Plugin> onDisable = new EventSignal<>();

    // APIs
    public Chat vaultChat;
    public InventoryActionDelegator inventoryGui;

    // State
    public ConfigLoading config;
    public GameManager gameManager;

    // Module loading
    @Override
    public void onEnable() {
        getLogger().info("JukeItOut enabling...");
        instance = this;

        // Get APIs
        vaultChat = Optional.ofNullable(getServer().getServicesManager().getRegistration(Chat.class))
                .map(RegisteredServiceProvider::getProvider).orElseThrow(() -> new IllegalStateException("Failed to get Vault chat service!"));

        inventoryGui = new InventoryActionDelegator();
        inventoryGui.bind(this);

        // Bind listeners
        registerListener(new GlobalProtect());
        registerListener(new GameEvents());
        CommandRegistrar.bind();

        // Build state
        config = new ConfigLoading();
        gameManager = new GameManager();

        // Fire them up!
        onEnable.fire(this);
        getLogger().info("JukeItOut enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JukeItOut disabling...");
        onDisable.fire(this);
        getLogger().info("JukeItOut disabled!");
        instance = null;
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
