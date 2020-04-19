package net.coopfury.JukeItOut.modules.gameManager.state;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.spigot.MessageUtils;
import net.coopfury.JukeItOut.modules.configLoading.schema.ConfigSchemaLocation;
import net.coopfury.JukeItOut.modules.configLoading.schema.ConfigSchemaTeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class PlayTeam {
    final ConfigSchemaTeam teamConfig;
    final List<PlayTeamMember> members = new ArrayList<>();

    public PlayTeam(ConfigSchemaTeam teamConfig) {
        this.teamConfig = teamConfig;
    }

    public PlayTeamMember registerMember(Player player) {
        PlayTeamMember member = new PlayTeamMember(player, this);
        members.add(member);
        return member;
    }

    public void teleportToSpawnRoundBegin(PlayModeGame gameState) {  // TODO: Null handling.
        for (PlayTeamMember member: members) {
            Player player = member.player;
            ConfigSchemaLocation location = teamConfig.spawnLocation.value;
            if (location != null) {
                player.teleport(location.deserializedLocation);
                MessageUtils.playTitle(player, String.format(Constants.message_game_round_title, gameState.round));
            }
        }
    }
}
