package net.coopfury.JukeItOut.state.game.playing.teams;

import net.coopfury.JukeItOut.utils.game.BaseTeamManager;
import net.coopfury.JukeItOut.utils.game.MemberPair;
import net.coopfury.JukeItOut.utils.java.RandomUtils;
import net.coopfury.JukeItOut.utils.spigot.UiUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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

    public void formatAppendTeamName(StringBuilder builder, Player player) {
        Optional<MemberPair<GameTeam, GameMember>> memberPair = getMemberPair(player.getUniqueId());

        if (!memberPair.isPresent() || !memberPair.get().member.isAlive) {
            builder.append(ChatColor.GRAY).append("[SPECTATOR] ");
        }

        memberPair.ifPresent(pair -> builder.append(pair.team.getTextColor().orElse(ChatColor.WHITE))
                .append("[")
                .append(pair.team.configTeam.getName().orElse("UNNAMED").toUpperCase())
                .append("] "));
    }

    public String formatPlayerName(Player player) {
        StringBuilder builder = new StringBuilder();
        formatAppendTeamName(builder, player);
        builder.append(UiUtils.formatVaultName(player));
        return builder.toString();
    }

    public void tick() {
        for (GameTeam team : getTeams()) {
            team.tick(this);
        }
    }

    public void cleanup() {
        for (GameTeam team : getTeams()) {
            team.cleanup();
        }
    }
}