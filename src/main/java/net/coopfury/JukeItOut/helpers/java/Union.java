package net.coopfury.JukeItOut.helpers.java;

import java.util.Optional;
import java.util.function.Consumer;

public final class Union<TBase> {
    public TBase value;

    public Union(TBase value) {
        this.value = value;
    }

    public<TValue extends TBase> Union<TBase> If(Class<TValue> type, Consumer<TValue> handler) {
        CastUtils.tryCast(type, value, handler);
        return this;
    }

    public<TValue extends TBase> Optional<TValue> Get(Class<TValue> type) {
        return CastUtils.dynamicCast(type, value);
    }

    public<TValue extends TBase> boolean Is(Class<TValue> type) {
        return Get(type).isPresent();
    }
}
