package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.helpers.config.ConfigPrimitives;
import net.coopfury.JukeItOut.helpers.java.CastUtils;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigTeam {
    public final Map<String, Object> map;

    public ConfigTeam() {
        this.map = new HashMap<>();
    }

    public ConfigTeam(Map<String, Object> map) {
        this.map = map;
    }


    public Optional<String> getName() {
        return CastUtils.getMap(map, String.class, "name");
    }

    public void setName(String newName) {
        map.replace("name", newName);
    }


    public Optional<Integer> getWoolColor() {
        return CastUtils.getMap(map, Integer.class, "color");
    }

    public void setWoolColor(int data) {
        map.replace("color", data);
    }


    public Optional<Location> getSpawnLocation() {
        return ConfigPrimitives.unpackLocation(map.getOrDefault("spawn", null));
    }

    public void setSpawnLocation(Location location) {
        map.replace("spawn", ConfigPrimitives.packLocation(location));
    }


    public boolean isValid() {
        return getName().isPresent() && getSpawnLocation().isPresent();
    }
}
