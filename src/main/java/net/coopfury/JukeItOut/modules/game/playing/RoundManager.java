package net.coopfury.JukeItOut.modules.game.playing;

import net.coopfury.JukeItOut.helpers.java.TimeUnits;
import net.coopfury.JukeItOut.helpers.java.TimestampUtils;
import net.coopfury.JukeItOut.helpers.java.signal.ProcedureSignal;

class RoundManager {
    // Config
    private final long TIME_PRE_SPAWN = TimeUnits.Secs.encode(20);
    private final long TIME_POST_SPAWN = TimeUnits.Secs.encode(25);
    private final int MAX_TRADES = 3;
    private final long TIME_TRADE_ADD = TimeUnits.Secs.encode(15);

    // Signals
    private final ProcedureSignal onDiamondSpawned = new ProcedureSignal();
    private final ProcedureSignal onRoundReset = new ProcedureSignal();

    // Round state description
    private int roundNumber;
    private long nextEventAt;
    private boolean hasDiamondSpawned;
    private int tradesLeft = MAX_TRADES;

    // Time querying
    public int getRoundNumber() {
        return roundNumber;
    }

    public boolean isDefenseRound() {
        return roundNumber % 5 == 0;
    }

    public long getTimeMsLeft() {
        return TimestampUtils.getTimeUntil(nextEventAt, TimeUnits.Ms) + (hasDiamondSpawned ? 0 : TIME_PRE_SPAWN);
    }

    // Event handling
    public void nextRound() {
        roundNumber++;
        nextEventAt = TimestampUtils.getTimeIn(TIME_PRE_SPAWN);
        hasDiamondSpawned = false;
        tradesLeft = MAX_TRADES;
        onRoundReset.fire();
    }

    public void diamondTraded() {
        assert hasDiamondSpawned;

        if (tradesLeft > 0) {
            tradesLeft--;
            nextEventAt += TIME_TRADE_ADD;
        }
    }

    public void tick() {
        if (hasDiamondSpawned) {
            if (TimestampUtils.hasOccurred(nextEventAt)) {
                nextRound();
            }
        } else {
            if (TimestampUtils.hasOccurred(nextEventAt)) {
                hasDiamondSpawned = true;
                nextEventAt += TIME_POST_SPAWN;
                onDiamondSpawned.fire();
            }
        }
    }
}
