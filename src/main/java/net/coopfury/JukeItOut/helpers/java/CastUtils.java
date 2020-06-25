package net.coopfury.JukeItOut.helpers.java;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public final class CastUtils {
    @SuppressWarnings("unchecked")  // The cast is checked here but IntelliJ doesn't recognize it.
    public static<T> Optional<T> dynamicCast(Class<T> type, Object object) {
        if (type.isInstance(object)) {
            return Optional.of((T) object);
        } else {
            return Optional.empty();
        }
    }

    public static<T, D> void tryCast(Class<T> type, D unCasted, Consumer<T> ifSuccess, Consumer<D> ifFailure) {
        Optional<T> casted = dynamicCast(type, unCasted);
        if (casted.isPresent()) {
            ifSuccess.accept(casted.get());
        } else {
            ifFailure.accept(unCasted);
        }
    }

    public static<T, D> void tryCast(Class<T> type, D unCasted, Consumer<T> ifSuccess) {
        tryCast(type, unCasted, ifSuccess, e -> {});
    }

    public static<K, V> Optional<V> getMap(Map<K, ?> map, Class<V> type, K key) {
        return dynamicCast(type, map.getOrDefault(key, null));
    }
}
