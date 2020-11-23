package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.helpers.gui.InventoryActionDelegator;
import net.coopfury.JukeItOut.helpers.java.signal.EventSignal;
import net.coopfury.JukeItOut.modules.GlobalProtect;
import net.coopfury.JukeItOut.modules.commands.CommandRegistrar;
import net.coopfury.JukeItOut.modules.config.ConfigLoading;
import net.coopfury.JukeItOut.modules.game.GameManager;
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

    // Modules
    public InventoryActionDelegator inventoryGui;
    public GlobalProtect globalProtect;
    public ConfigLoading config;
    public CommandRegistrar commands;
    public GameManager gameManager;

    // Module loading
    @Override
    public void onEnable() {
        getLogger().info("JukeItOut enabling...");
        instance = this;

        // Get APIs
        vaultChat = Optional.ofNullable(getServer().getServicesManager().getRegistration(Chat.class))
                .map(RegisteredServiceProvider::getProvider).orElseThrow(() -> new IllegalStateException("Failed to get Vault chat service!"));

        // Build modules
        inventoryGui = new InventoryActionDelegator();
        globalProtect = new GlobalProtect();
        config = new ConfigLoading();
        commands = new CommandRegistrar();
        gameManager = new GameManager();

        // Fire them up!
        inventoryGui.bind(this);
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

    public void registerListener(Listener listener) {

    }

    public void unregisterListener(Listener listener) {

    }
}
