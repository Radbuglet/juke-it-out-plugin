package net.coopfury.JukeItOut.modules.configLoading.schema;

import net.coopfury.JukeItOut.helpers.config.ConfigPipelineCore;
import net.coopfury.JukeItOut.helpers.config.ConfigSchema;
import net.coopfury.JukeItOut.helpers.java.Box;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("location")
public class ConfigSchemaLocation extends ConfigSchema {
    public Location deserializedLocation;
    protected ConfigSchemaLocation(Map<String, Object> data) {
        super(data);
    }

    @Override
    protected void serializePipelined(Pipeline pipeline) {
        if (deserializedLocation == null) return;
        pipeline.runSerializer(writeTo -> {
            writeTo.put("world", deserializedLocation.getWorld().getName());
            return true;
        });
        pipeline.runSerializer(writeTo -> {
            writeTo.put("x", deserializedLocation.getX());
            return true;
        });
        pipeline.runSerializer(writeTo -> {
            writeTo.put("y", deserializedLocation.getY());
            return true;
        });
        pipeline.runSerializer(writeTo -> {
            writeTo.put("z", deserializedLocation.getZ());
            return true;
        });
        pipeline.runSerializer(writeTo -> {
            writeTo.put("pitch", deserializedLocation.getPitch());
            return true;
        });
        pipeline.runSerializer(writeTo -> {
            writeTo.put("yaw", deserializedLocation.getYaw());
            return true;
        });
    }

    @Override
    protected void deserializePipelined(Pipeline pipeline) {
        Box<World> worldBox = new Box<>(null);
        Box<Double> xBox = new Box<>(0d);
        Box<Double> yBox = new Box<>(0d);
        Box<Double> zBox = new Box<>(0d);
        Box<Float> pitchBox = new Box<>(0f);
        Box<Float> yawBox = new Box<>(0f);
        ConfigPipelineCore.deserializeTypedParam(pipeline, "world", String.class, worldName -> {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                worldBox.value = world;
                return true;
            }
            return false;
        });
        ConfigPipelineCore.relayTypedBox(pipeline, "x", Double.class, xBox);
        ConfigPipelineCore.relayTypedBox(pipeline, "y", Double.class, yBox);
        ConfigPipelineCore.relayTypedBox(pipeline, "z", Double.class, zBox);
        ConfigPipelineCore.relayTypedBox(pipeline, "pitch", Float.class, pitchBox);
        ConfigPipelineCore.relayTypedBox(pipeline, "yaw", Float.class, yawBox);

        deserializedLocation = pipeline.isSuccessful() ?
                new Location(worldBox.value, xBox.value, yBox.value, zBox.value, pitchBox.value, yawBox.value) : null;
    }

    public boolean hasValidLocation() {
        return deserializedLocation != null;
    }
}
