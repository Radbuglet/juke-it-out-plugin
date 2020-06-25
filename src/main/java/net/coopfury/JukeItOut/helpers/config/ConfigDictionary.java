package net.coopfury.JukeItOut.helpers.config;

import net.coopfury.JukeItOut.helpers.java.EnumerableUtils;

import java.util.*;
import java.util.function.Function;

public class ConfigDictionary<T> {
    private final Map<String, Object> map;
    private final Function<Object, Optional<T>> wrapper;

    public ConfigDictionary(Map<String, Object> map, Function<Object, Optional<T>> wrapper) {
        this.map = map;
        this.wrapper = wrapper;
    }

    public ConfigDictionary(Function<Object, Optional<T>> wrapper) {
        this.map = new HashMap<>();
        this.wrapper = wrapper;
    }

    public void replace(String key, Object underlyingData) {
        map.replace(key, underlyingData);
    }

    public void clear() {
        map.clear();
    }

    public Optional<T> get(String key) {
        Object valueRaw = map.getOrDefault(key, null);
        return valueRaw == null ? Optional.empty() : wrapper.apply(valueRaw);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Collection<Optional<T>> values() {
        Collection<Object> rawValueCollection = map.values();
        return new AbstractCollection<Optional<T>>() {
            @Override
            public Iterator<Optional<T>> iterator() {
                return EnumerableUtils.map(rawValueCollection.iterator(), wrapper);
            }

            @Override
            public int size() {
                return rawValueCollection.size();
            }
        };
    }
}
