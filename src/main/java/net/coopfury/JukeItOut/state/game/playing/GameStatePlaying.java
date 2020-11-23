package net.coopfury.JukeItOut.state.game.playing;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.state.game.GameState;
import net.coopfury.JukeItOut.state.game.playing.teams.TeamManager;

public class GameStatePlaying implements GameState {
    public final TeamManager teamManager = new TeamManager();

    public final RoundManager roundManager = new RoundManager(this);
    public final DiamondManager diamondManager = new DiamondManager(this);
    public final WorldReset worldReset = new WorldReset(this);

    @Override
    public void onActivate() {
        Plugin.instance.getLogger().info("Game start (Fight!)");
    }

    @Override
    public void onDeactivate(boolean pluginDisable) {
        worldReset.resetWorld(true);
        teamManager.cleanup();
        roundManager.cleanup();
    }

    @Override
    public void onTick() {
        roundManager.tick();
        diamondManager.tick();
    }
}
