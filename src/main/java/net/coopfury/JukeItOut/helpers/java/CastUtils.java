package net.coopfury.JukeItOut.helpers.java;

import java.util.Optional;

public final class CastUtils {
    @SuppressWarnings("unchecked")  // The cast is checked here but IntelliJ doesn't recognize it.
    public static<T> Optional<T> dynamicCast(Class<T> type, Object object) {
        if (type.isInstance(object)) {
            return Optional.of((T) object);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Double> interpretDouble(Object value) {
        return value instanceof Number ? Optional.of(((Number) value).doubleValue()) : Optional.empty();
    }

    public static Optional<Float> interpretFloat(Object value) {
        return value instanceof Number ? Optional.of(((Number) value).floatValue()) : Optional.empty();
    }
}
