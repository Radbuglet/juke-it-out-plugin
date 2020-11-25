package net.coopfury.JukeItOut.state.game.playing.teams;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.state.config.ConfigTeam;
import net.coopfury.JukeItOut.state.game.playing.GameStatePlaying;
import net.coopfury.JukeItOut.utils.game.BaseTeamManager;
import net.coopfury.JukeItOut.utils.game.MemberPair;
import net.coopfury.JukeItOut.utils.java.RandomUtils;
import net.coopfury.JukeItOut.utils.spigot.ScoreboardUtils;
import net.coopfury.JukeItOut.utils.spigot.UiUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class TeamManager extends BaseTeamManager<GameTeam, GameMember> {
    private final GameStatePlaying root;
    private final Map<UUID, GameTeam> offlinePlayerTeams = new HashMap<>();
    private final Objective guiObjective;

    public TeamManager(GameStatePlaying root) {
        this.root = root;

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        guiObjective = ScoreboardUtils.obtainObjective(scoreboard, "cf_diamonds_gui", ChatColor.GOLD + "Team Diamonds", "dummy");
        guiObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Optional<ConfigTeam> configTeam : Plugin.instance.config.getRoot().getTeams().values()) {
            configTeam.ifPresent(conf -> {
                GameTeam team = new GameTeam(conf, guiObjective);
                addTeam(team);
            });
        }
    }

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

    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Optional<MemberPair<GameTeam, GameMember>> memberPair =
                getMemberPair(player.getUniqueId());

        if (!memberPair.isPresent()) return;
        if (!memberPair.get().member.isAlive) {
            event.setCancelled(true);
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Find owner
        GameTeam owningTeam = null;
        for (GameTeam team: getTeams()) {
            Optional<Location> teamLocation;
            switch (clickedBlock.getType()) {
                case CHEST:
                    teamLocation = team.configTeam.getChestLocation();
                    break;
                case JUKEBOX:
                    teamLocation = team.configTeam.getJukeboxLocation();
                    break;
                default:
                    return;  // Never mind, the clicked block isn't a chest or a jukebox. Sorry for wasting your time, iterator.
            }

            if (teamLocation.isPresent() && teamLocation.get().getBlock().equals(clickedBlock)) {
                owningTeam = team;
                break;
            }
        }

        if (owningTeam == null) {
            player.sendMessage(ChatColor.RED + "You can't open the diamond reserves of empty teams.");
            player.playSound(event.getClickedBlock().getLocation(), Sound.DOOR_OPEN, 1, 1);
            event.setCancelled(true);
            return;
        }

        // Check defense round
        if (!root.roundManager.isDefenseRound() && owningTeam != memberPair.get().team) {
            player.sendMessage(ChatColor.RED + "You can only open your team's diamond reserves on non-defense rounds.");
            player.playSound(event.getClickedBlock().getLocation(), Sound.DOOR_OPEN, 1, 1);
            event.setCancelled(true);
            return;
        }

        // Handle normal logic (only does anything for jukebox)
        if (clickedBlock.getType() == Material.JUKEBOX) {
            event.setCancelled(true);

            if (owningTeam == memberPair.get().team) {
                owningTeam.openJukebox(player);
            } else {
                owningTeam.stealDiamonds(player);
            }
        }
    }

    public void tick() {
        for (GameTeam team : getTeams()) {
            team.tick(this);
        }
    }

    public void cleanup() {
        guiObjective.unregister();
        for (GameTeam team : getTeams()) {
            team.cleanup();
        }
    }
}