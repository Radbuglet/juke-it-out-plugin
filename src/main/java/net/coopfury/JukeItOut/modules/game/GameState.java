package net.coopfury.JukeItOut.modules.game;

public interface GameState {
    default void onActivate() {}
    default void onDeactivate(boolean pluginDisable) {}
    default void onTick() {}
}
