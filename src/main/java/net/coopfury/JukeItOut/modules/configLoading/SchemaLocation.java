package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.helpers.config.ConfigSchema;
import net.coopfury.JukeItOut.helpers.config.DeserializationException;
import net.coopfury.JukeItOut.helpers.config.SerializedFormatPipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("location")
public class SchemaLocation extends ConfigSchema {
    public Location location = new Location(null, 0d, 0d, 0d);

    public SchemaLocation(Map<String, Object> configSection) throws DeserializationException {
        loadFromConfig(configSection);
    }

    public SchemaLocation(Location location) {
        this.location = location;
        isValid = true;
    }

    @Override
    protected void handleFormat(SerializedFormatPipe format) throws DeserializationException {
        format.field("world", String.class, new SerializedFormatPipe.DelegateAdvancedHandler<String>() {
            @Override
            public Object serialize() {
                return location.getWorld().getName();
            }

            @Override
            public void deserializeAndSet(String worldName) throws DeserializationException {
                location.setWorld(Bukkit.getWorld(worldName));
                if (location.getWorld() == null) throw new DeserializationException(String.format("Failed to find world named \"%s\"", worldName));
            }
        });

        format.field("x", Double.class, location::setX, location::getX);
        format.field("y", Double.class, location::setY, location::getY);
        format.field("z", Double.class, location::setZ, location::getZ);
        format.field("h", Double.class, val -> location.setPitch((float) (double) val), location::getPitch);
        format.field("v", Double.class, val -> location.setYaw((float) (double) val), location::getYaw);
    }
}
