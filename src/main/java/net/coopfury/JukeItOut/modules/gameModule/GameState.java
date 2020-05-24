package net.coopfury.JukeItOut.modules.gameModule;

import org.bukkit.event.Listener;

public interface GameState extends Listener {
    void tick();
}
