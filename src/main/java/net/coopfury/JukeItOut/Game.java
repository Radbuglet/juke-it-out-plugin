package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.helpers.java.CastUtils;
import net.coopfury.JukeItOut.modules.SmallFixesModule;
import net.coopfury.JukeItOut.modules.adminCommands.AdminCommandModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.gameManager.GameManagerModule;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Game extends JavaPlugin {
    // Properties
    private static Game instance;
    private final Map<Class<?>, Object> registeredModules = new HashMap<>();

    // Module loading
    private void getModules(Consumer<GameModule> moduleConsumer) {
        moduleConsumer.accept(new SmallFixesModule());
        moduleConsumer.accept(new ConfigLoadingModule());
        moduleConsumer.accept(new AdminCommandModule());
        moduleConsumer.accept(new GameManagerModule());
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
        getModules(module -> module.onDisable(this));

        getLogger().info("JukeItOut disabled!");
    }

    // Module tracking
    public void registerModule(GameModule instance, PluginManager pluginManager) {
        assert !registeredModules.containsKey(instance.getClass());
        registeredModules.put(instance.getClass(), instance);
        instance.onEnable(this);
        pluginManager.registerEvents(instance, this);
    }

    public<T extends GameModule> T _getModule(Class<T> type) {  // Named this way as to avoid naming conflicts with the static relays.
        return CastUtils.dynamicCast(type, registeredModules.get(type)).orElseThrow(IllegalAccessError::new);
    }

    // Static relays
    public static<T extends GameModule> T getModule(Class<T> type) {
        return instance._getModule(type);
    }

    public static Game getGame() {
        return instance;
    }
}
