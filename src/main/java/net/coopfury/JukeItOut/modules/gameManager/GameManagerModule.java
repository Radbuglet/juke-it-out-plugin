package net.coopfury.JukeItOut.modules.gameManager;

import net.coopfury.JukeItOut.Game;
import net.coopfury.JukeItOut.GameModule;
import net.coopfury.JukeItOut.helpers.java.CastUtils;
import net.coopfury.JukeItOut.modules.gameManager.state.PlayModeGame;
import net.coopfury.JukeItOut.modules.gameManager.state.PlayModeWaiting;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Optional;

public class GameManagerModule extends GameModule {
    // Play mode controller
    private Object playMode = new PlayModeGame();

    Optional<PlayModeWaiting> getModeWaiting() {
        if (playMode instanceof PlayModeWaiting) {
            return Optional.of((PlayModeWaiting) playMode);
        } else {
            return Optional.empty();
        }
    }

    Optional<PlayModeGame> getModeGame() {
        if (playMode instanceof PlayModeGame) {
            return Optional.of((PlayModeGame) playMode);
        } else {
            return Optional.empty();
        }
    }

    // Event handling
    @EventHandler
    private void handleDamage(EntityDamageEvent event) {
        CastUtils.tryCast(Player.class, event.getEntity(), player -> {
            if (getModeWaiting().isPresent()) {
                event.setCancelled(true);
            } else if (getModeGame().isPresent()) {
                boolean isKilled = player.getHealth() - event.getFinalDamage() <= 0;
                if (isKilled) {
                    event.setDamage(0);
                    player.sendMessage(ChatColor.RED + "Bro, you died. That's kinda cringe.");
                }
            }
        });
    }
}
