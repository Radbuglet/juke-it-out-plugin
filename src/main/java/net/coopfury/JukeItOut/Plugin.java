package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.helpers.gui.InventoryActionDelegator;
import net.coopfury.JukeItOut.helpers.java.CastUtils;
import net.coopfury.JukeItOut.modules.GlobalFixesModule;
import net.coopfury.JukeItOut.modules.adminCommands.AdminCommandModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.gameModule.GameModule;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Plugin extends JavaPlugin {
    // Properties
    public static Plugin instance;
    public static Chat vaultChat;
    public static InventoryActionDelegator inventoryGui;
    private final List<PluginModule> registeredModulesList = new ArrayList<>();
    private final Map<Class<?>, PluginModule> registeredModulesMap = new HashMap<>();

    // Module loading
    @Override
    public void onEnable() {
        getLogger().info("JukeItOut enabling...");
        instance = this;

        // Setup external services
        vaultChat = Optional.ofNullable(getServer().getServicesManager().getRegistration(Chat.class))
            .map(RegisteredServiceProvider::getProvider).orElseThrow(() -> new IllegalStateException("Failed to get Vault chat service!"));
        inventoryGui = new InventoryActionDelegator();
        inventoryGui.bind(this);

        // Register and initialize modules
        PluginManager pluginManager = getServer().getPluginManager();
        registerModule(new GlobalFixesModule(), pluginManager);
        registerModule(new ConfigLoadingModule(), pluginManager);
        registerModule(new AdminCommandModule(), pluginManager);
        registerModule(new GameModule(), pluginManager);

        getLogger().info("JukeItOut enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JukeItOut disabling...");
        for (PluginModule module: registeredModulesList) {
            module.onDisable(this);
        }
        getLogger().info("JukeItOut disabled!");
        instance = null;
    }

    public void bindListener(Listener listener, PluginManager pluginManager) {
        pluginManager.registerEvents(listener, this);
    }

    public void bindListener(Listener listener) {
        bindListener(listener, getServer().getPluginManager());
    }

    // Module tracking
    public void registerModule(PluginModule instance, PluginManager pluginManager) {
        assert !registeredModulesMap.containsKey(instance.getClass());
        registeredModulesMap.put(instance.getClass(), instance);
        instance.onEnable(this);
        bindListener(instance, pluginManager);
        registeredModulesList.add(instance);
    }

    public<T extends PluginModule> T _getModule(Class<T> type) {  // Named this way as to avoid naming conflicts with the static relays.
        return CastUtils.dynamicCast(type, registeredModulesMap.get(type)).orElseThrow(IllegalAccessError::new);
    }

    // Static relays
    public static<T extends PluginModule> T getModule(Class<T> type) {
        return instance._getModule(type);
    }
}
