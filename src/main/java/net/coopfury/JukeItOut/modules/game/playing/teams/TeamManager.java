package net.coopfury.JukeItOut.modules.game.playing.teams;

import net.coopfury.JukeItOut.helpers.game.BaseTeamManager;
import net.coopfury.JukeItOut.helpers.game.MemberPair;
import net.coopfury.JukeItOut.helpers.java.RandomUtils;

import java.util.*;

public class TeamManager extends BaseTeamManager<GameTeam, GameMember> {
    private final Map<UUID, GameTeam> offlinePlayerTeams = new HashMap<>();

    public Optional<MemberPair<GameTeam, GameMember>> addPlayerToGame(UUID playerId) {
        // Select a team
        GameTeam selectedTeam = offlinePlayerTeams.get(playerId);
        if (selectedTeam == null) {
            // Find the teams in need of population
            List<GameTeam> equalTeams = new ArrayList<>();
            int minPlayerCount = Integer.MAX_VALUE;

            for (GameTeam team : getTeams()) {
                int size = team.getMembers().size();
                if (size < minPlayerCount) {
                    minPlayerCount = size;
                    equalTeams.clear();
                }

                if (size == minPlayerCount) {
                    equalTeams.add(team);
                }
            }

            // Choose a random team if there are multiple with the same count
            selectedTeam = RandomUtils.randomElement(equalTeams);
        } else {
            // Unmark the player as offline
            offlinePlayerTeams.remove(playerId);
        }

        if (selectedTeam == null) {
            return Optional.empty();  // Abort!
        }

        // Add them to the team
        GameMember member = new GameMember(playerId);
        addMemberInto(playerId, selectedTeam, member);
        return Optional.of(new MemberPair<>(selectedTeam, member));
    }

    public void removePlayerFromGame(UUID playerId) {
        Optional<GameTeam> team = getMemberTeam(playerId);
        if (!team.isPresent()) {
            return;
        }

        // Store which team they were on
        offlinePlayerTeams.put(playerId, team.get());

        // Remove the player
        removeMember(playerId);
    }
}