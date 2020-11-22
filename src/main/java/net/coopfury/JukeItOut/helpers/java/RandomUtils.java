package net.coopfury.JukeItOut.helpers.java;

import java.util.List;
import java.util.Random;

public final class RandomUtils {
    private static final Random random = new Random();

    public static int randInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static<T> T randomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
