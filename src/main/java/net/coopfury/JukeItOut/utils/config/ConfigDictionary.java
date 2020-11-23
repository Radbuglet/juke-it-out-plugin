package net.coopfury.JukeItOut.utils.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

/**
 * A direct wrapper around a dictionary like object in a Bukkit configuration. Automatically wraps all kv-pairs in the specified
 * type safe wrapper. Wrappers can only operate on dictionary like objects.
 * @param <T> The specified wrapper type.
 */
public class ConfigDictionary<T> {
    public final ConfigurationSection rootSection;
    private final Function<ConfigurationSection, T> wrapper;

    /**
     * @param rootSection: The root section of the config where the dictionary actually lives.
     * @param wrapper: A function which takes a nested configuration section and wraps it.
     */
    public ConfigDictionary(ConfigurationSection rootSection, Function<ConfigurationSection, T> wrapper) {
        this.rootSection = rootSection;
        this.wrapper = wrapper;
    }

    /**
     * Creates an empty configuration section and wraps it in the specified wrapper type. If the section key is already
     * in the map, it will be overridden.
     * @param key: The key of the section to create.
     * @return A wrapper around the new section.
     */
    public T create(String key) {
        return wrapper.apply(rootSection.createSection(key));
    }

    /**
     * Removes a section from the map. Fails silently if it didn't yet exist.
     */
    public void remove(String key) {
        rootSection.set(key, null);
    }

    /**
     * Returns whether or not the config contains a key. If the key does not contain a wrappable section, an empty optional
     * will be returned.
     */
    public boolean has(String key) {
        return rootSection.isConfigurationSection(key);
    }

    /**
     * Returns a collection of all keys in the section. For performance reasons, this does not filter out invalid keys.
     */
    public Collection<String> keys() {
        return rootSection.getKeys(false);
    }

    /**
     * Gets a value in the dictionary and wraps it. If the value isn't present or is not another configuration section,
     * an empty optional will be returned.
     */
    public Optional<T> get(String key) {
        ConfigurationSection subSection = rootSection.getConfigurationSection(key);
        return subSection != null ? Optional.of(wrapper.apply(subSection)) : Optional.empty();
    }

    /**
     * An iterator of values contained in the dictionary. If it fails to wrap a value in the section, it will return an
     * empty optional.
     */
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
