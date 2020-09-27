package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.helpers.config.ConfigDictionary;
import net.coopfury.JukeItOut.helpers.config.ConfigUtils;
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
        ConfigUtils.setLocation(ConfigUtils.readOrMakeSection(section, "diamond_spawn"), location);
    }

    public Optional<Location> getDiamondSpawn() {
        return ConfigUtils.getLocation(section.getConfigurationSection("diamond_spawn"));
    }

    public void setLobbySpawn(Location location) {
        ConfigUtils.setLocation(ConfigUtils.readOrMakeSection(section, "lobby"), location);
    }

    public Optional<Location> getLobbySpawn() {
        return ConfigUtils.getLocation(section.getConfigurationSection("lobby"));
    }

    public Optional<World> getGameWorld() {
        return getDiamondSpawn().map(Location::getWorld);
    }
}
