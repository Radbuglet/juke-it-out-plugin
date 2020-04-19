package net.coopfury.JukeItOut.modules.gameManager.state;

import org.bukkit.entity.Player;

class PlayTeamMember {
    final Player player;
    final PlayTeam team;

    public PlayTeamMember(Player player, PlayTeam team) {
        this.player = player;
        this.team = team;
    }
}
