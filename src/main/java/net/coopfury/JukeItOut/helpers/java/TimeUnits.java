package net.coopfury.JukeItOut.helpers.java;

public enum TimeUnits {
    Ms(1),
    Ticks(1000 / 20),
    Secs(1000);

    public final int unitMultiplier;
    TimeUnits(int unitMultiplier) {
        this.unitMultiplier = unitMultiplier;
    }

    public long encode(int val) {
        return val * unitMultiplier;
    }

    public long decode(long val) {
        return val / unitMultiplier;
    }
}