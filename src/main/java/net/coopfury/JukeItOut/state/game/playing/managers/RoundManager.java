package net.coopfury.JukeItOut.state.game.playing.managers;

import net.coopfury.JukeItOut.state.game.playing.GameStatePlaying;
import net.coopfury.JukeItOut.utils.java.TimeUnits;
import net.coopfury.JukeItOut.utils.java.TimestampUtils;
import net.coopfury.JukeItOut.utils.java.signal.ProcedureSignal;
import net.coopfury.JukeItOut.state.game.playing.teams.GameMember;
import org.bukkit.entity.Player;

public class RoundManager {
    // Config
    private final long TIME_PRE_SPAWN = TimeUnits.Secs.encode(20);
    private final long TIME_POST_SPAWN = TimeUnits.Secs.encode(15);
    private final int MAX_TRADES = 3;
    private final long TIME_TRADE_ADD = TimeUnits.Secs.encode(10);

    // Signals
    public final ProcedureSignal onDiamondSpawned = new ProcedureSignal();  // TODO: Why does this trigger so early?
    public final ProcedureSignal onRoundEnd = new ProcedureSignal();

    // Properties
    private final GameStatePlaying root;
    private int roundNumber;
    private long nextEventAt;
    private boolean hasDiamondSpawned;
    private int tradesLeft = MAX_TRADES;

    public RoundManager(GameStatePlaying root) {
        this.root = root;
    }

    // Time querying
    public int getRoundNumber() {
        return roundNumber;
    }

    public boolean isDefenseRound() {
        return roundNumber % 5 == 0;
    }

    public boolean hasDiamondSpawned() {
        return hasDiamondSpawned;
    }

    public long getTimeMsLeft() {
        return TimestampUtils.getTimeUntil(nextEventAt, TimeUnits.Ms) + (hasDiamondSpawned ? 0 : TIME_POST_SPAWN);
    }

    // Event handling
    public void nextRound() {
        // Update state
        roundNumber++;
        nextEventAt = TimestampUtils.getTimeIn(TIME_PRE_SPAWN);
        hasDiamondSpawned = false;
        tradesLeft = MAX_TRADES;
        onRoundEnd.fire();

        // Update visuals (boss-bar)
        // TODO
    }

    public void diamondTraded() {
        assert hasDiamondSpawned;

        if (tradesLeft > 0) {
            tradesLeft--;
            nextEventAt += TIME_TRADE_ADD;
        }
    }

    public void tick() {
        // Update state
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

        // Update visuals (experience)
        long msLeft = getTimeMsLeft();
        int secsLeft = (int) TimeUnits.Secs.decode(msLeft) + 1;
        float percentLeft = (msLeft % TimeUnits.Secs.unitMultiplier) / (float) TimeUnits.Secs.unitMultiplier;

        for (GameMember member : root.teamManager.getMembers()) {
            if (!member.isAlive) continue;
            Player player = member.getPlayer();
            player.setLevel(secsLeft);
            player.setExp(percentLeft);
        }
    }

    public void cleanup() {
        // TODO: Remove boss-bar
    }
}
