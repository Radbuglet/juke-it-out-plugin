package net.coopfury.JukeItOut.utils.java;

public enum TimeUnits {
    Ms(1),
    Ticks(1000 / 20),
    Secs(1000);

    public final int unitMultiplier;
    TimeUnits(int unitMultiplier) {
        this.unitMultiplier = unitMultiplier;
    }

    public long encode(long val) {
        return val * unitMultiplier;
    }

    public long decode(long val) {
        return val / unitMultiplier;
    }
}