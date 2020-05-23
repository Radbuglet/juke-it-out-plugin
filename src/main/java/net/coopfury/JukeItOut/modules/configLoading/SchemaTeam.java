package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.helpers.config.ConfigSchema;
import net.coopfury.JukeItOut.helpers.config.DeserializationException;
import net.coopfury.JukeItOut.helpers.config.SerializedFormatPipe;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("team")
public class SchemaTeam extends ConfigSchema {
    public String name;
    public int colorDamageId;
    public SchemaLocation spawnLocation;

    public SchemaTeam(Map<String, Object> configSection) throws DeserializationException {
        loadFromConfig(configSection);
    }

    public SchemaTeam(String name, int colorDamageId, Location spawnLocation) {
        this.name = name;
        this.colorDamageId = colorDamageId;
        this.spawnLocation = new SchemaLocation(spawnLocation);
        isValid = true;
    }

    @Override
    protected void handleFormat(SerializedFormatPipe format) throws DeserializationException {
        format.field("name", String.class, v -> name = v, () -> name);
        format.field("color", Integer.class, v -> colorDamageId = v, () -> colorDamageId);
        format.field("spawn", SchemaLocation.class, v -> spawnLocation = v, () -> spawnLocation, loc ->
                DeserializationException.asserted(loc.isValid, "Invalid spawn location!"));
    }
}
