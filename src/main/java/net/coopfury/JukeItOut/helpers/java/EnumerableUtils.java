package net.coopfury.JukeItOut.helpers.java;

import java.util.Iterator;
import java.util.function.Function;

public final class EnumerableUtils {
    public static<TOriginal, TMapped> Iterator<TMapped> map(Iterator<TOriginal> iterator, Function<TOriginal, TMapped> mapper) {
        return new Iterator<TMapped>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public TMapped next() {
                return mapper.apply(iterator.next());
            }
        };
    }
}
