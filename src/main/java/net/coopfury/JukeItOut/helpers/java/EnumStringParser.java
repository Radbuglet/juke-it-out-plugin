package net.coopfury.JukeItOut.helpers.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EnumStringParser<T> {
    private final Map<String, T> mapping = new HashMap<>();

    public EnumStringParser<T> addMapping(String id, T target) {
        mapping.put(id.toLowerCase(), target);
        return this;
    }

    public Optional<T> parse(String id) {
        return Optional.ofNullable(mapping.getOrDefault(id.toLowerCase(), null));
    }

    public boolean isValid(String id) {
        return mapping.containsKey(id);
    }

    public Set<String> getValidValues() {
        return mapping.keySet();
    }
}
