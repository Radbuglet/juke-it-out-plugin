package net.coopfury.JukeItOut.helpers.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

// TODO: Ensure that no primitives other than sections make it into the map
public class ConfigDictionary<T> {
    public final ConfigurationSection rootSection;
    private final Function<ConfigurationSection, T> wrapper;

    public ConfigDictionary(ConfigurationSection rootSection, Function<ConfigurationSection, T> wrapper) {
        this.rootSection = rootSection;
        this.wrapper = wrapper;
    }

    public T create(String key) {
        return wrapper.apply(rootSection.createSection(key));
    }

    public void remove(String key) {
        rootSection.set(key, null);
    }

    public boolean has(String key) {
        return rootSection.isConfigurationSection(key);
    }

    public Collection<String> keys() {
        return rootSection.getKeys(false);
    }

    public Optional<T> get(String key) {
        ConfigurationSection subSection = rootSection.getConfigurationSection(key);
        return subSection != null ? Optional.of(wrapper.apply(subSection)) : Optional.empty();
    }

    public Iterable<Optional<T>> values() {
        return () -> new Iterator<Optional<T>>() {
            private final Iterator<String> keys = keys().iterator();

            @Override
            public boolean hasNext() {
                return keys.hasNext();
            }

            @Override
            public Optional<T> next() {
                return get(keys.next());
            }
        };
    }

    public Object getRaw(String key) {
        return rootSection.get(key);
    }

    public void setRaw(String key, Object value) {
        rootSection.set(key, value);
    }
}
