package net.coopfury.JukeItOut.modules.gameModule.playing.teams;

import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

// TODO: Internal team management should be a generic util for future projects.
public class TeamManager {
    private final List<GameTeam> teams = new ArrayList<>();
    private final Map<UUID, GameTeamMember> memberMap = new HashMap<>();

    // Registration
    public GameTeam makeTeam(ConfigTeam teamConfig) {
        GameTeam team = new GameTeam(teamConfig);
        teams.add(team);
        return team;
    }

    void internalRegisterMember(GameTeamMember member) {
        memberMap.put(member.playerUuid, member);
    }

    public void removeMember(GameTeamMember member) {
        memberMap.remove(member.playerUuid);
        member.team.internalUnregisterMember(member);
    }

    // Querying
    public Optional<GameTeamMember> getMember(Player player) {
        return Optional.ofNullable(memberMap.get(player.getUniqueId()));
    }

    public Collection<GameTeam> getTeams() {
        return teams;
    }

    public Collection<GameTeamMember> getMembers() {
        return memberMap.values();
    }

    // Name formatting
    public void formatAppendTeamName(StringBuilder builder, Player player) {
        Optional<GameTeamMember> member = getMember(player);
        if (!member.isPresent() || !member.get().isAlive) {
            builder.append(ChatColor.GRAY).append("[SPECTATOR] ");
        }
        member.ifPresent(gameTeamMember -> builder.append(gameTeamMember.team.getTextColor().orElse(ChatColor.WHITE))
                .append("[")
                .append(gameTeamMember.team.configTeam.getName().orElse("UNNAMED").toUpperCase())
                .append("] "));
    }

    public String formatPlayerName(Player player) {
        StringBuilder builder = new StringBuilder();
        formatAppendTeamName(builder, player);
        builder.append(UiUtils.formatVaultName(player));
        return builder.toString();
    }
}
