package net.coopfury.JukeItOut.helpers.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EnumConverter<TFrom, TTo> {
    private final Map<TFrom, TTo> mapping = new HashMap<>();

    public EnumConverter<TFrom, TTo> addMapping(TFrom from, TTo target) {
        mapping.put(from, target);
        return this;
    }

    public Optional<TTo> parse(TFrom id) {
        return Optional.ofNullable(mapping.getOrDefault(id, null));
    }

    public boolean isValid(TFrom id) {
        return mapping.containsKey(id);
    }

    public Set<TFrom> getValidValues() {
        return mapping.keySet();
    }
}
