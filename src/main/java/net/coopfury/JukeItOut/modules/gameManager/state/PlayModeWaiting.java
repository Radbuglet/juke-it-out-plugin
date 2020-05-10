package net.coopfury.JukeItOut.modules.gameManager.state;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayModeWaiting implements PlayModeCommon {
    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void tick() {

    }
}
