package net.coopfury.JukeItOut.helpers.java;

import java.util.Optional;

// Eww, inheritance that doesnt't strictly extend. Unfortunately, this is the easiest way to quickly relay a bunch of functions.
public class EnumStringConverter<T> extends EnumConverter<String, T> {
    @Override
    public EnumConverter<String, T> addMapping(String s, T target) {
        return super.addMapping(s.toLowerCase(), target);
    }

    @Override
    public boolean isValid(String id) {
        return super.isValid(id.toLowerCase());
    }

    @Override
    public Optional<T> parse(String id) {
        return super.parse(id.toLowerCase());
    }
}
