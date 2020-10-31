package net.coopfury.JukeItOut.helpers.virtualCommand;

import java.util.*;

public class CommandContext {
    public static final class Symbol<T> {
        public final UUID key = UUID.randomUUID();
    }

    private final Stack<Map<UUID, Object>> frames = new Stack<>();

    public void pushFrame() {
        frames.add(new HashMap<>());
    }

    public void popFrame() {
        frames.pop();
    }

    public<T> void postData(Symbol<T> symbol, T value) {
        // peek() will yield an EmptyStackException if we can't post the data into a frame.
        frames.peek().put(symbol.key, value);
    }

    @SuppressWarnings("unchecked")
    public<T> Optional<T> getData(Symbol<T> symbol) {
        for (int index = frames.size() - 1; index >= 0; index--) {
            Object result = frames.get(index).get(symbol.key);
            if (result != null) {
                return Optional.of((T) result);
            }
        }
        return Optional.empty();
    }
}
