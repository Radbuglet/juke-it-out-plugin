package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.helpers.config.ConfigDictionary;
import net.coopfury.JukeItOut.helpers.config.ConfigOptionalUtils;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigRoot {
    private final ConfigurationSection section;

    public ConfigRoot(ConfigurationSection section) {
        this.section = section;
    }

    public ConfigDictionary<ConfigTeam> getTeams() {
        return new ConfigDictionary<>(ConfigOptionalUtils.readOrMakeSection(section, "teams"), ConfigTeam::new);
    }
}
