package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.helpers.config.ConfigPrimitives;
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
        return ConfigPrimitives.getLocation(section.getConfigurationSection("spawn"));  // getLocation accepts null sections
    }

    public void setSpawnLocation(Location location) {
        ConfigPrimitives.setLocation(ConfigUtils.readOrMakeSection(section, "spawn"), location);
    }

    public boolean isValid() {
        return getName().isPresent() && getSpawnLocation().isPresent() && getWoolColor().isPresent();
    }
}
