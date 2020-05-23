package net.coopfury.JukeItOut.helpers.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigSchema implements ConfigurationSerializable {
    public boolean isValid;

    protected abstract void handleFormat(SerializedFormatPipe format) throws DeserializationException;

    protected void loadFromConfig(Map<String, Object> configSection) {
        boolean success;
        try {
            handleFormat(new SerializedFormatPipe(SerializedFormatPipe.Mode.DESERIALIZE, configSection));
            success = true;
        } catch (DeserializationException e) {
            success = false;
        }
        isValid = success;
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

    public static<T extends ConfigSchema> void registerSchema(Class<T> schemaClass) {
        ConfigurationSerialization.registerClass(schemaClass);
    }
}
