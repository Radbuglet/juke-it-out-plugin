package net.coopfury.JukeItOut.helpers.java;

public final class TimestampUtils {
    public static long getTimeNow() {
        return System.currentTimeMillis();
    }

    public static boolean hasOccurred(long timestamp) {
        return getTimeNow() > timestamp;
    }

    public static long getTimeIn(TimeUnits uint, int value) {
        return getTimeNow() + uint.encode(value);
    }

    public static long getTimeUntil(long timestamp, TimeUnits uint) {
        return uint.decode(timestamp - getTimeNow()) + 1;
    }
}
