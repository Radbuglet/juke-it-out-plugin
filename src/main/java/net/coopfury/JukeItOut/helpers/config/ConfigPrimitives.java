package net.coopfury.JukeItOut.helpers.config;

import net.coopfury.JukeItOut.helpers.java.CastUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ConfigPrimitives {
    public static Optional<Map<String, Object>> unpackMap(Object mapRaw) {
        if (!(mapRaw instanceof Map)) return Optional.empty();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) mapRaw;
        return Optional.of(map);
    }

    public static Map<String, Object> packLocation(Location location) {
        Map<String, Object> map = new HashMap<>();
        map.put("world", location.getWorld());
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getY());
        map.put("pitch", location.getPitch());
        map.put("yaw", location.getYaw());
        return map;
    }

    public static Optional<Location> unpackLocation(Object mapRaw) {
        // Cast map
        Optional<Map<String, Object>> map = unpackMap(mapRaw);
        if (!map.isPresent()) return Optional.empty();

        // Get world
        Optional<String> worldName = CastUtils.getMap(map.get(), String.class, "world");
        if (!worldName.isPresent()) return Optional.empty();
        World world = Bukkit.getWorld(worldName.get());
        if (world == null) return Optional.empty();

        // Get numeric values
        Optional<Double> x = CastUtils.getMap(map.get(), Double.class, "x");
        if (!x.isPresent()) return Optional.empty();

        Optional<Double> y = CastUtils.getMap(map.get(), Double.class, "y");
        if (!y.isPresent()) return Optional.empty();

        Optional<Double> z = CastUtils.getMap(map.get(), Double.class, "z");
        if (!z.isPresent()) return Optional.empty();

        Optional<Double> pitch = CastUtils.getMap(map.get(), Double.class, "pitch");
        if (!pitch.isPresent()) return Optional.empty();

        Optional<Double> yaw = CastUtils.getMap(map.get(), Double.class, "yaw");
        //noinspection OptionalIsPresent
        if (!yaw.isPresent()) return Optional.empty();

        // Construct location
        return Optional.of(new Location(world, x.get(), y.get(), z.get(),
                (float) (double) (pitch.get()), (float) (double) (yaw.get())));
    }
}
