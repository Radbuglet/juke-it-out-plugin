package net.coopfury.JukeItOut.helpers.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public final class ConfigPrimitives {
    public static void setLocation(ConfigurationSection section, Location location) {
        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("pitch", location.getPitch());
        section.set("yaw", location.getYaw());
    }

    public static Optional<Location> getLocation(ConfigurationSection section) {
        // Get world
        String worldName = section.getString("world");
        if (worldName == null) return Optional.empty();
        World world = Bukkit.getWorld(worldName);
        if (world == null) return Optional.empty();

        // Ensure that all numeric fields are present
        if (!(section.isDouble("x") && section.isDouble("y") && section.isDouble("z") &&
                section.isDouble("pitch") && section.isDouble("yaw"))) return Optional.empty();

        // Construct location
        return Optional.of(new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
                (float) section.getDouble("yaw"), (float) section.getDouble("pitch")));
    }
}
