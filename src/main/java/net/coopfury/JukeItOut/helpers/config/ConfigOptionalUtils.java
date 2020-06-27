package net.coopfury.JukeItOut.helpers.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.function.Function;

public final class ConfigOptionalUtils {
    public static<T> Optional<T> readGeneric(String key, Function<String, T> getter, Function<String, Boolean> validator) {
        return validator.apply(key) ? Optional.of(getter.apply(key)) : Optional.empty();
    }

    public static Optional<String> readString(ConfigurationSection section, String key) {
        return Optional.ofNullable(section.getString(key));
    }

    public static Optional<Integer> readInteger(ConfigurationSection section, String key) {
        return readGeneric(key, section::getInt, section::isInt);
    }

    public static Optional<Double> readDouble(ConfigurationSection section, String key) {
        return readGeneric(key, section::getDouble, section::isDouble);
    }

    public static ConfigurationSection readOrMakeSection(ConfigurationSection section, String key) {
        if (!section.isConfigurationSection(key)) {
            section.createSection(key);
        }
        return section.getConfigurationSection(key);
    }
}
