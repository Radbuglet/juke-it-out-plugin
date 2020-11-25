package net.coopfury.JukeItOut.state.game.playing;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.state.game.GameState;
import net.coopfury.JukeItOut.state.game.playing.managers.CombatManager;
import net.coopfury.JukeItOut.state.game.playing.managers.DiamondManager;
import net.coopfury.JukeItOut.state.game.playing.managers.RoundManager;
import net.coopfury.JukeItOut.state.game.playing.managers.WorldReset;
import net.coopfury.JukeItOut.state.game.playing.teams.TeamManager;
import net.coopfury.JukeItOut.utils.java.signal.SignalPriority;

public class GameStatePlaying implements GameState {
    // TODO: Forward events
    public final TeamManager teamManager = new TeamManager();
    public final RoundManager roundManager = new RoundManager(this);
    public final DiamondManager diamondManager = new DiamondManager(this);
    public final WorldReset worldReset = new WorldReset(this);
    public final CombatManager combatManager = new CombatManager(this);

    @Override
    public void onActivate() {
        Plugin.instance.getLogger().info("Game start (Fight!)");
        roundManager.onDiamondSpawned.connect(diamondManager::spawnDiamond, SignalPriority.Medium);
        roundManager.onRoundEnd.connect(diamondManager::resetRoundState, SignalPriority.Medium);
        roundManager.onRoundEnd.connect(() -> worldReset.resetWorld(false), SignalPriority.Medium);
        roundManager.onRoundEnd.connect(combatManager::startRound, SignalPriority.Low);
        diamondManager.onDiamondHolderChange.connect(holder -> roundManager.diamondTraded(), SignalPriority.Medium);
    }

    @Override
    public void onDeactivate(boolean pluginDisable) {
        worldReset.resetWorld(true);
        teamManager.cleanup();
        roundManager.cleanup();
    }

    @Override
    public void onTick() {
        teamManager.tick();
        roundManager.tick();

        if (roundManager.hasDiamondSpawned()) {
            diamondManager.tick();
        }
    }
}
