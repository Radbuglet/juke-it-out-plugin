package net.coopfury.JukeItOut.modules.configLoading.schema;

import net.coopfury.JukeItOut.helpers.config.ConfigSchema;
import net.coopfury.JukeItOut.helpers.config.DeserializationException;
import net.coopfury.JukeItOut.helpers.config.SerializedFormatPipe;
import net.coopfury.JukeItOut.helpers.java.Box;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("team")
public class SchemaTeam extends ConfigSchema {
    public final Box<String> Name = new Box<>(null);
    public final Box<Integer> ColorDamageId = new Box<>(null);
    public final Box<SchemaLocation> SpawnLocation = new Box<>(null);

    public SchemaTeam(Map<String, Object> configSection) throws DeserializationException {
        super(configSection);
    }

    @Override
    protected void handleFormat(SerializedFormatPipe format) throws DeserializationException {
        format.field("name", String.class, Name);
        format.field("color", Integer.class, ColorDamageId);
        format.field("spawn", SchemaLocation.class, SpawnLocation);
    }
}
