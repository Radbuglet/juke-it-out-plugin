package net.coopfury.JukeItOut.utils.java.signal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

abstract class BaseSignal<THandler> {
    private final List<Set<THandler>> handlerBuckets = new ArrayList<>();  // Ideally, we would use an array but they don't support generic elements.

    public BaseSignal() {
        for (int i = 0; i < 3; i++) {
            handlerBuckets.add(new HashSet<>());
        }
    }

    public void connect(THandler handler, SignalPriority priority) {
        handlerBuckets.get(priority.ordinal()).add(handler);
    }

    public void disconnect(THandler handler) {
        for (Set<?> bucket : handlerBuckets) {
            if (bucket.remove(handler)) break;
        }
    }

    protected void dispatch(Consumer<THandler> dispatcher) {
        for (Set<THandler> bucket : handlerBuckets) {
            bucket.forEach(dispatcher);
        }
    }
}
