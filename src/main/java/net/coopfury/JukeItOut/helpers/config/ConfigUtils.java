package net.coopfury.JukeItOut.helpers.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.function.Function;

public final class ConfigUtils {
    public static<T> Optional<T> readGeneric(String key, Function<String, T> getter, Function<String, Boolean> validator) {
        return validator.apply(key) ? Optional.of(getter.apply(key)) : Optional.empty();
    }

    public static Optional<String> readString(ConfigurationSection section, String key) {
        return Optional.ofNullable(section.getString(key));
    }

    public static Optional<Integer> readInteger(ConfigurationSection section, String key) {
        return readGeneric(key, section::getInt, section::isInt);
    }

    public static ConfigurationSection readOrMakeSection(ConfigurationSection section, String key) {
        if (!section.isConfigurationSection(key)) {
            section.createSection(key);
        }
        return section.getConfigurationSection(key);
    }

    public static void setLocation(ConfigurationSection section, Location location) {
        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("pitch", location.getPitch());
        section.set("yaw", location.getYaw());
    }

    public static Optional<Location> getLocation(ConfigurationSection section) {
        if (section == null) return Optional.empty();

        // Get world
        String worldName = section.getString("world");
        if (worldName == null) return Optional.empty();
        World world = Bukkit.getWorld(worldName);
        if (world == null) return Optional.empty();

        // Ensure that all numeric fields are present
        if (!(ConfigUtils.isNumeric(section, "x") && ConfigUtils.isNumeric(section, "y") && ConfigUtils.isNumeric(section, "z") &&
                ConfigUtils.isNumeric(section, "pitch") && ConfigUtils.isNumeric(section, "yaw")))
            return Optional.empty();

        // Construct location
        return Optional.of(new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
                (float) section.getDouble("yaw"), (float) section.getDouble("pitch")));
    }

    public static boolean isNumeric(ConfigurationSection section, String key) {
        return section.isDouble(key) || section.isInt(key);
    }
}
