package net.coopfury.JukeItOut.modules.gameModule.playing;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GameTeamMember {
    public final GameTeam team;
    public final UUID playerUuid;
    public boolean isAlive;

    public GameTeamMember(GameTeam team, UUID playerUuid) {
        this.team = team;
        this.playerUuid = playerUuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUuid);
    }
}
