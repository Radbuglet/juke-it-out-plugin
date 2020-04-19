package net.coopfury.JukeItOut.helpers.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigSchema implements ConfigurationSerializable  {
    // Pipeline classes
    public interface PipelineSerializer {
        boolean serialize(Map<String, Object> writeTo);
    }

    public interface PipelineDeserializer {
        boolean deserialize(Map<String, Object> readFrom);
    }

    public static class Pipeline {
        private final Map<String, Object> data;
        private final boolean modeSerializing;
        private boolean isPassing = true;

        public Pipeline(boolean modeSerializing, Map<String, Object> data) {  // (or else deserialize)
            this.data = data;
            this.modeSerializing = modeSerializing;
        }

        public<T extends PipelineSerializer & PipelineDeserializer> void runBoth(T handler) {
            if (!isPassing) return;
            if (modeSerializing) {
                isPassing = handler.serialize(data);
            } else {
                isPassing = handler.deserialize(data);
            }
        }

        public void runSerializer(PipelineSerializer handler) {
            if (!isPassing) return;
            if (modeSerializing) {
                isPassing = handler.serialize(data);
            }
        }

        public void runDeserializer(PipelineDeserializer handler) {
            if (!isPassing) return;
            if (!modeSerializing) {
                isPassing = handler.deserialize(data);
            }
        }

        public boolean isSuccessful() {
            return isPassing;
        }
    }

    // Serialization & deserialization forwarding
    protected ConfigSchema(Map<String, Object> data) {
        deserializePipelined(new Pipeline(false, data));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> outMap = new HashMap<>();
        serializePipelined(new Pipeline(true, outMap));
        return outMap;
    }

    protected abstract void serializePipelined(Pipeline pipeline);
    protected abstract void deserializePipelined(Pipeline pipeline);
}
