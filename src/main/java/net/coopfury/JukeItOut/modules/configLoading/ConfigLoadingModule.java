package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.helpers.config.ConfigSchema;
import net.coopfury.JukeItOut.helpers.config.SerializedFormatPipe;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigLoadingModule extends PluginModule {
    static {
        ConfigSchema.registerSchema(SchemaTeam.class);
        ConfigSchema.registerSchema(SchemaLocation.class);
    }

    public List<SchemaTeam> teams = new ArrayList<>();

    private FileConfiguration getConfig() {
        return Plugin.getGame().getConfig();
    }

    private Logger getLogger() {
        return Plugin.getGame().getLogger();
    }

    public void loadConfig() {
        FileConfiguration conf = getConfig();
        Logger logger = getLogger();
        logger.info("Loading config...");

        // Load teams
        logger.info("Loading teams...");
        teams = SerializedFormatPipe.loadListStatic(conf.getList(Constants.config_root_teams), SchemaTeam.class, logger::warning);
        logger.info(String.format("Loaded %s team%s.", teams.size(), teams.size() == 1 ? "" : "s"));

        logger.info("Loaded config.");
    }

    public void reloadConfig() {
        Plugin.getGame().reloadConfig();
        loadConfig();
    }

    public void saveConfig() {
        FileConfiguration conf = getConfig();
        Logger logger = getLogger();

        logger.info("Saving config...");

        conf.set(Constants.config_root_teams, teams);
        logger.info(String.format("Saved %s team%s.", teams.size(), teams.size() == 1 ? "" : "s"));

        Plugin.getGame().saveConfig();
        logger.info("Saved config.");
    }

    public int getTeamIndexByName(String name) {
        for (int index = 0; index < teams.size(); index++) {
            if (teams.get(index).name.equals(name))
                return index;
        }
        return -1;
    }

    @Override
    protected void onEnable(Plugin pluginInstance) {
        loadConfig();
    }

    @Override
    protected void onDisable(Plugin pluginInstance) {
        saveConfig();
    }
}
