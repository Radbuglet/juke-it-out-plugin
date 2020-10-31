package net.coopfury.JukeItOut.helpers.config;

import net.coopfury.JukeItOut.helpers.java.CastUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public final class ConfigPrimitives {
    public static Optional<String> readString(ConfigurationSection section, String key) {
        return Optional.ofNullable(section.getString(key));
    }

    public static Optional<Double> readNumericDouble(ConfigurationSection section, String key) {
        return CastUtils.interpretDouble(section.get(key));
    }

    public static Optional<Float> readNumericFloat(ConfigurationSection section, String key) {
        return CastUtils.interpretFloat(section.get(key));
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
        Optional<Double> x = readNumericDouble(section, "x");
        if (!x.isPresent()) return Optional.empty();

        Optional<Double> y = readNumericDouble(section, "y");
        if (!y.isPresent()) return Optional.empty();

        Optional<Double> z = readNumericDouble(section, "z");
        if (!z.isPresent()) return Optional.empty();

        Optional<Float> pitch = readNumericFloat(section, "pitch");
        if (!pitch.isPresent()) return Optional.empty();

        Optional<Float> yaw = readNumericFloat(section, "yaw");

        //noinspection OptionalIsPresent
        if (!yaw.isPresent()) return Optional.empty();

        // Construct location
        return Optional.of(new Location(world, x.get(), y.get(), z.get(), yaw.get(), pitch.get()));
    }
}
