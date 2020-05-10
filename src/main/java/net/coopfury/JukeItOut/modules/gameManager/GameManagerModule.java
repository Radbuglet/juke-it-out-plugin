package net.coopfury.JukeItOut.modules.gameManager;

import net.coopfury.JukeItOut.Game;
import net.coopfury.JukeItOut.GameModule;
import net.coopfury.JukeItOut.helpers.java.Union;
import net.coopfury.JukeItOut.modules.gameManager.state.PlayModeCommon;
import net.coopfury.JukeItOut.modules.gameManager.state.PlayModeGame;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManagerModule extends GameModule {
    // Play mode controller
    private final Union<PlayModeCommon> playMode = new Union<>(null);

    public void setPlayMode(Game pluginInstance, PlayModeCommon newMode) {
        if (playMode.value != null) {
            HandlerList.unregisterAll(playMode.value);
        }
        pluginInstance.bindListener(newMode);
        playMode.value = newMode;
    }

    // Event handling
    @Override
    protected void onEnable(Game pluginInstance) {
        {
            PlayModeGame initialPlayMode = new PlayModeGame();
            initialPlayMode.startRound();
            setPlayMode(pluginInstance, initialPlayMode);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                playMode.value.tick();
            }
        }.runTaskTimer(pluginInstance, 0, 0);
    }
}
