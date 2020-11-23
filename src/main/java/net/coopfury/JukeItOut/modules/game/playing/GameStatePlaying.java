package net.coopfury.JukeItOut.modules.game.playing;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.modules.game.GameState;
import net.coopfury.JukeItOut.modules.game.playing.teams.TeamManager;

public class GameStatePlaying implements GameState {
    public final RoundManager roundManager = new RoundManager(this);
    public final TeamManager teamManager = new TeamManager();
    public final WorldReset worldReset = new WorldReset(this);

    @Override
    public void onActivate() {
        Plugin.instance.getLogger().info("Game start (Fight!)");
    }

    @Override
    public void onTick() {
        roundManager.tick();
    }
}
