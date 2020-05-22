package net.coopfury.JukeItOut.helpers.config;

import net.coopfury.JukeItOut.helpers.java.Box;
import net.coopfury.JukeItOut.helpers.java.CastUtils;

import java.util.Map;
import java.util.Optional;

// Welcome to the sad world of EE Java coding
// TODO: Better error messages...
public class SerializedFormatPipe {
    public interface DelegateValidator<TDeserialized> {
        void validate(TDeserialized deserialized) throws DeserializationException;
    }

    public interface DelegateAdvancedHandler<TDeserialized> {
        Object serialize();
        void deserializeAndSet(TDeserialized unprocessed) throws DeserializationException;
    }

    public enum Mode {
        SERIALIZE,
        DESERIALIZE
    }

    private final Mode mode;
    private final Map<String, Object> targetSection;

    SerializedFormatPipe(Mode mode, Map<String, Object> targetSection) {
        this.mode = mode;
        this.targetSection = targetSection;
    }

    public<TDeserialized> void Field(String key, Class<TDeserialized> type, DelegateAdvancedHandler<TDeserialized> handler) throws DeserializationException {
        if (mode == Mode.SERIALIZE) {
            targetSection.put(key, handler.serialize());
        } else {
            Object raw = targetSection.getOrDefault(key, null);
            if (raw == null) {
                throw new DeserializationException("Failed to find config value in config section!");
            }
            Optional<TDeserialized> serializedOptional = CastUtils.dynamicCast(type, raw);
            if (serializedOptional.isPresent()) {
                handler.deserializeAndSet(serializedOptional.get());
            } else {
                throw new DeserializationException("Value in config section is of an invalid primitive type!");
            }
        }
    }

    public<TDeserialized> void Field(String key, Class<TDeserialized> type, Box<TDeserialized> targetBox) throws DeserializationException {
        Field(key, type, new DelegateAdvancedHandler<TDeserialized>() {
            @Override
            public Object serialize() {
                return targetBox.value;
            }

            @Override
            public void deserializeAndSet(TDeserialized unprocessed) throws DeserializationException {
                targetBox.value = unprocessed;
            }
        });
    }

    public<TDeserialized> void Field(String key, Class<TDeserialized> type, Box<TDeserialized> targetBox, DelegateValidator<TDeserialized> validator) throws DeserializationException {
        Field(key, type, new DelegateAdvancedHandler<TDeserialized>() {
            @Override
            public Object serialize() {
                return targetBox.value;
            }

            @Override
            public void deserializeAndSet(TDeserialized unprocessed) throws DeserializationException {
                validator.validate(unprocessed);
                targetBox.value = unprocessed;
            }
        });
    }
}
