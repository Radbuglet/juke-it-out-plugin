package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.configLoading.SchemaTeam;
import net.coopfury.JukeItOut.modules.gameModule.GameState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class GameStatePlaying implements GameState {
    private final List<GameTeam> teams = new ArrayList<>();
    private final Map<UUID, GameTeamMember> memberMap = new HashMap<>();
    private int roundId;
    private int roundTicksLeft;

    public void startRound() {
        roundId++;
        roundTicksLeft = 20 * 10;
        for (GameTeam team: teams) {
            for (GameTeamMember member: team.members) {
                Player player = member.getPlayer();
                player.teleport(team.configTeam.spawnLocation.location);
                player.setHealth(player.getMaxHealth());
                player.setGameMode(GameMode.SURVIVAL);
                UiUtils.playTitle(player, String.format(Constants.message_game_new_round, roundId), Constants.title_timings_important);
                UiUtils.playSound(player, Constants.sound_new_round);
            }
        }
    }

    @Override
    public void tick() {
        roundTicksLeft--;
        if (roundTicksLeft < 0) {
            startRound();
            return;
        }
        for (GameTeamMember member: memberMap.values()) {
            Player player = member.getPlayer();
            player.setExp((float) (roundTicksLeft % 20) / 20);
            player.setLevel(roundTicksLeft / 20);
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

    private void removeMember(GameTeamMember member) {
        memberMap.remove(member.playerUuid);
        member.team.unregisterMember(member);
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameTeamMember member = memberMap.getOrDefault(player.getUniqueId(), null);
        if (member != null) {
            removeMember(member);
        }
    }
}
