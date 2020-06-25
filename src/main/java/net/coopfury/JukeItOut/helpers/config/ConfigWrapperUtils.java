package net.coopfury.JukeItOut.helpers.config;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class ConfigWrapperUtils {
    public static<T> Function<Object, Optional<T>> createStructWrapper(Function<Map<String, Object>, T> wrapper) {
        return raw -> {
            Optional<Map<String, Object>> map = ConfigPrimitives.unpackMap(raw);
            return map.map(wrapper);  // Shorthand for `map.isPresent() ? Optional.of(wrapper.apply(map.get())) : Optional.empty()`?!
        };
    }
}
