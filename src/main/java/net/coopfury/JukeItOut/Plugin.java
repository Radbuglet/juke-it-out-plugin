package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.helpers.java.CastUtils;
import net.coopfury.JukeItOut.modules.SmallFixesModule;
import net.coopfury.JukeItOut.modules.adminCommands.AdminCommandModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.gameModule.GameModule;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Plugin extends JavaPlugin {
    // Properties
    private static Plugin instance;
    private final List<PluginModule> registeredModulesList = new ArrayList<>();
    private final Map<Class<?>, PluginModule> registeredModulesMap = new HashMap<>();

    // Module loading
    private void getModules(Consumer<PluginModule> moduleConsumer) {
        moduleConsumer.accept(new SmallFixesModule());
        moduleConsumer.accept(new AdminCommandModule());
        moduleConsumer.accept(new ConfigLoadingModule());
        moduleConsumer.accept(new GameModule());
    }

    @Override
    public void onEnable() {
        // Setup core
        getLogger().info("JukeItOut enabling...");
        instance = this;

        // Register and initialize modules
        PluginManager pluginManager = getServer().getPluginManager();
        getModules(module -> registerModule(module, pluginManager));
        getLogger().info("JukeItOut enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JukeItOut disabling...");
        for (PluginModule module: registeredModulesList) {
            module.onDisable(this);
        }
        getLogger().info("JukeItOut disabled!");
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

    public static Plugin getGame() {
        return instance;
    }
}
