package net.coopfury.JukeItOut.state.config;

import net.coopfury.JukeItOut.utils.config.ConfigDictionary;
import net.coopfury.JukeItOut.utils.config.ConfigPrimitives;
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
        return new ConfigDictionary<>(ConfigPrimitives.readOrMakeSection(section, "teams"), ConfigTeam::new);
    }

    public void setDiamondSpawn(Location location) {
        ConfigPrimitives.setLocation(ConfigPrimitives.readOrMakeSection(section, "diamond_spawn"), location);
    }

    public Optional<Location> getDiamondSpawn() {
        return ConfigPrimitives.getLocation(section.getConfigurationSection("diamond_spawn"));
    }

    public Optional<World> getGameWorld() {
        return getDiamondSpawn().map(Location::getWorld);
    }
}