package net.coopfury.JukeItOut.utils.java.signal;

import java.util.function.Consumer;

public class EventSignal<T> extends BaseSignal<Consumer<T>> {
    public void fire(T arg) {
        dispatch(e -> e.accept(arg));
    }
}
