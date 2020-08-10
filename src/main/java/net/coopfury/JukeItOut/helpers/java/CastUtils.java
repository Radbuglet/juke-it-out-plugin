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
}
