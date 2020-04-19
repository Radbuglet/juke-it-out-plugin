package net.coopfury.JukeItOut.helpers.config;

import net.coopfury.JukeItOut.helpers.java.Box;
import net.coopfury.JukeItOut.helpers.java.CastUtils;

import java.util.Optional;
import java.util.function.Function;

public final class ConfigPipelineCore {
    public static<T> void deserializeTypedParam(ConfigSchema.Pipeline pipeline, String key, Class<T> requiredType, Function<T, Boolean> handler) {
        pipeline.runDeserializer(readFrom -> {
            Optional<T> valueUnCast = CastUtils.dynamicCast(requiredType, readFrom.get(key));
            if (valueUnCast.isPresent()) {
                return handler.apply(valueUnCast.get());
            } else {
                return false;
            }
        });
    }

    public static<T> void relayTypedBox(ConfigSchema.Pipeline pipeline, String key, Class<T> requiredType, Box<T> relayedTo) {
        deserializeTypedParam(pipeline, key, requiredType, data -> {
            relayedTo.value = data;
            return true;
        });
        pipeline.runSerializer(writeTo -> {
            writeTo.put(key, relayedTo.value);
            return true;
        });
    }
}
