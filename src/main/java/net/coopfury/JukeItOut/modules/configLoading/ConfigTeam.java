package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.helpers.config.ConfigUtils;
import net.coopfury.JukeItOut.helpers.spigot.SpigotEnumConverters;
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
        return ConfigUtils.readString(section, "name");
    }

    public void setName(String newName) {
        section.set("name", newName);
    }

    public Optional<DyeColor> getWoolColor() {
        return ConfigUtils.readString(section, "color").flatMap(SpigotEnumConverters.DYE_COLOR::parse);
    }

    public void setWoolColor(String id) {
        section.set("color", id);
    }

    public Optional<Location> getSpawnLocation() {
        return ConfigUtils.getLocation(section.getConfigurationSection("spawn"));  // getLocation accepts null sections
    }

    public void setSpawnLocation(Location location) {
        ConfigUtils.setLocation(ConfigUtils.readOrMakeSection(section, "spawn"), location);
    }

    public Optional<Location> getJukeboxLocation() {
        return ConfigUtils.getLocation(section.getConfigurationSection("jukebox"));
    }

    public void setJukeboxLocation(Location location) {
        ConfigUtils.setLocation(ConfigUtils.readOrMakeSection(section, "jukebox"), location);
    }

    public Optional<Location> getChestLocation() {
        return ConfigUtils.getLocation(section.getConfigurationSection("chest"));
    }

    public void setChestLocation(Location location) {
        ConfigUtils.setLocation(ConfigUtils.readOrMakeSection(section, "chest"), location);
    }

    public boolean isValid() {
        return getName().isPresent() && getWoolColor().isPresent()
                && getSpawnLocation().isPresent()
                && getJukeboxLocation().isPresent()
                && getChestLocation().isPresent();
    }
}
