package net.coopfury.JukeItOut.modules.game.playing;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.modules.game.GameState;

public class GameStatePlaying implements GameState {
    @Override
    public void onActivate() {
        Plugin.instance.getLogger().info("Yay!");
    }
}
