package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.modules.configLoading.schema.SchemaTeam;
import net.coopfury.JukeItOut.modules.gameModule.GameState;
import org.bukkit.entity.Player;

import java.util.*;

public class GameStatePlaying implements GameState {
    private final List<GameTeam> teams = new ArrayList<>();
    private final Map<UUID, GameTeamMember> memberMap = new HashMap<>();

    public void startRound() {
        for (GameTeam team: teams) {
            for (GameTeamMember member: team.members) {
                Player player = member.getPlayer();
                player.teleport(team.configTeam.spawnLocation.location);
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    public GameTeam makeTeam(SchemaTeam teamConfig) {
        GameTeam team = new GameTeam(teamConfig);
        teams.add(team);
        return team;
    }

    void registerMember(GameTeamMember member) {
        memberMap.put(member.playerUuid, member);
    }
}
