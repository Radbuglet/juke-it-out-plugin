package net.coopfury.JukeItOut.state.config;

import net.coopfury.JukeItOut.utils.config.ConfigPrimitives;
import net.coopfury.JukeItOut.utils.spigot.SpigotEnumConverters;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public class ConfigTeam {
    public final ConfigurationSection section;

    public ConfigTeam(ConfigurationSection section) {
        this.section = section;
    }

    public Optional<String> getName() {
        return ConfigPrimitives.readString(section, "name");
    }

    public void setName(String newName) {
        section.set("name", newName);
    }

    public Optional<DyeColor> getWoolColor() {
        return ConfigPrimitives.readString(section, "color").flatMap(SpigotEnumConverters.DYE_COLOR::parse);
    }

    public void setWoolColor(String id) {
        section.set("color", id);
    }

    public Optional<Location> getSpawnLocation() {
        return ConfigPrimitives.getLocation(section.getConfigurationSection("spawn"));  // getLocation accepts null sections
    }

    public void setSpawnLocation(Location location) {
        ConfigPrimitives.setLocation(ConfigPrimitives.readOrMakeSection(section, "spawn"), location);
    }

    public Optional<Location> getJukeboxLocation() {
        return ConfigPrimitives.getLocation(section.getConfigurationSection("jukebox"));
    }

    public void setJukeboxLocation(Location location) {
        ConfigPrimitives.setLocation(ConfigPrimitives.readOrMakeSection(section, "jukebox"), location);
    }

    public Optional<Location> getChestLocation() {
        return ConfigPrimitives.getLocation(section.getConfigurationSection("chest"));
    }

    public void setChestLocation(Location location) {
        ConfigPrimitives.setLocation(ConfigPrimitives.readOrMakeSection(section, "chest"), location);
    }
}