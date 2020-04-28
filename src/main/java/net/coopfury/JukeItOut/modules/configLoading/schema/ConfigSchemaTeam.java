package net.coopfury.JukeItOut.modules.configLoading.schema;

import net.coopfury.JukeItOut.helpers.config.ConfigPipelineCore;
import net.coopfury.JukeItOut.helpers.config.ConfigSchema;
import net.coopfury.JukeItOut.helpers.java.Box;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("team")
public class ConfigSchemaTeam extends ConfigSchema {
    public boolean isValid;
    public Box<String> name;
    public Box<ConfigSchemaLocation> spawnLocation;
    public Box<ConfigSchemaLocation> jukeboxLocation;

    protected ConfigSchemaTeam(Map<String, Object> data) {
        super(data);
    }

    private void runPipelineLogic(Pipeline pipeline) {
        ConfigPipelineCore.relayTypedBox(pipeline, "name", String.class, name);
        ConfigPipelineCore.relayTypedBox(pipeline, "spawn", ConfigSchemaLocation.class, spawnLocation);
        ConfigPipelineCore.relayTypedBox(pipeline, "jukebox", ConfigSchemaLocation.class, jukeboxLocation);
    }

    @Override
    protected void serializePipelined(Pipeline pipeline) {
        runPipelineLogic(pipeline);
    }

    @Override
    protected void deserializePipelined(Pipeline pipeline) {
        runPipelineLogic(pipeline);
        isValid = pipeline.isSuccessful();
    }
}
