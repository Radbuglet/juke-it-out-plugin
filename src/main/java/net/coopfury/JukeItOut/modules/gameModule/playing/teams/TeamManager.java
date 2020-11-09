package net.coopfury.JukeItOut.modules.gameModule.playing.teams;

import net.coopfury.JukeItOut.helpers.game.BaseTeamManager;
import net.coopfury.JukeItOut.helpers.game.MemberPair;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TeamManager extends BaseTeamManager<GameTeam, GameTeamMember> {
    public void formatAppendTeamName(StringBuilder builder, Player player) {
        Optional<MemberPair<GameTeam, GameTeamMember>> memberPair = getMemberPair(player.getUniqueId());

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
}
