package net.coopfury.JukeItOut.modules.game.playing.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GameMember {
    public final UUID playerUuid;
    public boolean isAlive;

    public GameMember(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUuid);
    }
}
