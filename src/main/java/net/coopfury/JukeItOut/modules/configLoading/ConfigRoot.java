package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.helpers.config.ConfigDictionary;
import net.coopfury.JukeItOut.helpers.config.ConfigPrimitives;
import net.coopfury.JukeItOut.helpers.config.ConfigWrapperUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigRoot {
    private final Map<String, Object> map;

    public ConfigRoot(Map<String, Object> map) {
        this.map = map;
    }

    public ConfigDictionary<ConfigTeam> getTeams() {
        Optional<Map<String, Object>> rawTeamMap = ConfigPrimitives.unpackMap(map.get("teams"));
        if (!rawTeamMap.isPresent()) {
            rawTeamMap = Optional.of(new HashMap<>());
            map.put("teams", rawTeamMap.get());
        }
        return new ConfigDictionary<>(rawTeamMap.get(), ConfigWrapperUtils.createStructWrapper(ConfigTeam::new));
    }
}
