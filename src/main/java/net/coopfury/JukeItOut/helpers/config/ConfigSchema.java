package net.coopfury.JukeItOut.helpers.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigSchema implements ConfigurationSerializable {
    protected abstract void handleFormat(SerializedFormatPipe format) throws DeserializationException;

    public ConfigSchema(Map<String, Object> configSection) throws DeserializationException {
        handleFormat(new SerializedFormatPipe(SerializedFormatPipe.Mode.DESERIALIZE, configSection));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializedResult = new HashMap<>();
        try {
            handleFormat(new SerializedFormatPipe(SerializedFormatPipe.Mode.SERIALIZE, serializedResult));
        } catch (DeserializationException e) {
            e.printStackTrace();
            throw new IllegalStateException("Impossible case in serialization logic: got a deserialization exception!");
        }
        return serializedResult;
    }
}
