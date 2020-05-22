package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.helpers.config.ConfigSchema;
import net.coopfury.JukeItOut.modules.configLoading.schema.SchemaLocation;
import net.coopfury.JukeItOut.modules.configLoading.schema.SchemaTeam;

public class ConfigLoadingModule extends PluginModule {
    static {
        ConfigSchema.registerSchema(SchemaTeam.class);
        ConfigSchema.registerSchema(SchemaLocation.class);
    }

    @Override
    protected void onEnable(Plugin pluginInstance) {
        
    }
}
