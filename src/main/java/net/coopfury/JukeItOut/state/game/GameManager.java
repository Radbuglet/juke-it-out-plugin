package net.coopfury.JukeItOut.state.game;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.utils.java.CastUtils;
import net.coopfury.JukeItOut.utils.java.signal.SignalPriority;
import net.coopfury.JukeItOut.state.game.playing.GameStatePlaying;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class GameManager {
    private GameState activeState;

    public GameManager() {
        // Setup events
        new BukkitRunnable() {
            @Override
            public void run() {
                onTick();
            }
        }.runTaskTimer(Plugin.instance, 0, 0);
        Plugin.instance.onDisable.connect(this::onPluginDisable, SignalPriority.High);

        // Setup initial state
        Plugin.instance.onEnable.connect(plugin -> setActiveState(new GameStatePlaying()), SignalPriority.Low);
    }

    private void onTick() {
        if (activeState != null) activeState.onTick();
    }

    private void onPluginDisable(Plugin plugin) {
        activeState.onDeactivate(true);
    }

    public void setActiveState(GameState newState) {
        if (activeState != null) {
            activeState.onDeactivate(false);
        }
        (activeState = newState).onActivate();
    }

    public<T> Optional<T> getActiveState(Class<T> type) {
        return CastUtils.dynamicCast(type, activeState);
    }
}
