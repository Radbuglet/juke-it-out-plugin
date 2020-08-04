package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.helpers.config.ConfigDictionary;
import net.coopfury.JukeItOut.helpers.config.ConfigUtils;
import net.coopfury.JukeItOut.helpers.config.ConfigPrimitives;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public class ConfigRoot {
    private final ConfigurationSection section;

    public ConfigRoot(ConfigurationSection section) {
        this.section = section;
    }

    public ConfigDictionary<ConfigTeam> getTeams() {
        return new ConfigDictionary<>(ConfigUtils.readOrMakeSection(section, "teams"), ConfigTeam::new);
    }

    public void setDiamondSpawn(Location location) {
        ConfigPrimitives.setLocation(ConfigUtils.readOrMakeSection(section, "diamond_spawn"), location);
    }

    public Optional<Location> getDiamondSpawn() {
        return ConfigPrimitives.getLocation(section.getConfigurationSection("diamond_spawn"));
    }

    public Optional<World> getGameWorld() {
        return getDiamondSpawn().map(Location::getWorld);
    }
}
