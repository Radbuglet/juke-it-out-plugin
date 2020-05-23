package net.coopfury.JukeItOut.helpers.config;

import net.coopfury.JukeItOut.helpers.java.CastUtils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    public static<K, V, T extends V> T readFromMap(Map<K, V> map, K key, Class<T> type) throws DeserializationException {
        Object raw = map.getOrDefault(key, null);
        if (raw == null) {
            throw new DeserializationException("Failed to find config value in config section!");
        }
        Optional<T> serializedOptional = CastUtils.dynamicCast(type, raw);
        if (serializedOptional.isPresent()) {
            return serializedOptional.get();
        } else {
            throw new DeserializationException("Value in config section is of an invalid primitive type!");
        }
    }

    public<TDeserialized> void field(String key, Class<TDeserialized> type, DelegateAdvancedHandler<TDeserialized> handler) throws DeserializationException {
        if (mode == Mode.SERIALIZE) {
            targetSection.put(key, handler.serialize());
        } else {
            handler.deserializeAndSet(readFromMap(targetSection, key, type));
        }
    }

    public<TDeserialized> void field(String key, Class<TDeserialized> type, Consumer<TDeserialized> setter, Supplier<Object> getter) throws DeserializationException {
        field(key, type, new DelegateAdvancedHandler<TDeserialized>() {
            @Override
            public Object serialize() {
                return getter.get();
            }

            @Override
            public void deserializeAndSet(TDeserialized unprocessed) {
                setter.accept(unprocessed);
            }
        });
    }

    public<TDeserialized> void field(String key, Class<TDeserialized> type, Consumer<TDeserialized> setter, Supplier<Object> getter, DelegateValidator<TDeserialized> validator) throws DeserializationException {
        field(key, type, new DelegateAdvancedHandler<TDeserialized>() {
            @Override
            public Object serialize() {
                return getter.get();
            }

            @Override
            public void deserializeAndSet(TDeserialized unprocessed) throws DeserializationException {
                validator.validate(unprocessed);
                setter.accept(unprocessed);
            }
        });
    }
}
