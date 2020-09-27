package net.coopfury.JukeItOut.modules.gameModule.lobby;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.java.TimeUnits;
import net.coopfury.JukeItOut.helpers.java.TimestampUtils;
import net.coopfury.JukeItOut.helpers.spigot.PlayerUtils;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.GameModule;
import net.coopfury.JukeItOut.modules.gameModule.GameState;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameStatePlaying;
import net.coopfury.JukeItOut.modules.gameModule.playing.teams.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

// TODO: This is temporary!
public class GameStateLobby implements GameState {
    private boolean starting = false;
    private long gameStartTimestamp;
    private long lastAnnouncedSecond;

    public GameStateLobby() {
        for (Player player: Bukkit.getOnlinePlayers()) {
            teleportPlayerToLobby(player);
        }
        onPlayerCountUpdated(false);
    }

    private void teleportPlayerToLobby(Player player) {
        PlayerUtils.resetPlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        Plugin.getModule(ConfigLoadingModule.class).root.getLobbySpawn().ifPresent(player::teleport);
    }

    private void onPlayerCountUpdated(boolean playerHasQuit) {
        if (Bukkit.getOnlinePlayers().size() - (playerHasQuit ? 1 : 0) > 1) {
            if (!starting) {
                starting = true;
                announceCountdownStatus(ChatColor.GREEN + "Starting...", ChatColor.GREEN + "The game will start soon.");
                gameStartTimestamp = TimestampUtils.getTimeIn(TimeUnits.Secs, 15);
            }
        } else {
            if (starting) {
                starting = false;
                announceCountdownStatus(ChatColor.RED + "Cancelled!", ChatColor.RED + "Start cancelled (not enough players)");
            }
        }
    }

    private void announceCountdownStatus(String title, String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
            UiUtils.playTitle(player, title, "", Constants.title_timings_long);
            UiUtils.playSound(player, Sound.CLICK);
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        onPlayerCountUpdated(false);
        teleportPlayerToLobby(event.getPlayer());
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        onPlayerCountUpdated(true);
    }

    @Override
    public void onTick() {
        if (starting) {
            long secsUntilStart = TimestampUtils.getTimeUntil(gameStartTimestamp, TimeUnits.Secs);
            if (secsUntilStart == lastAnnouncedSecond) return;
            lastAnnouncedSecond = secsUntilStart;

            // TODO: This is garbage!!!
            if (secsUntilStart == 10) {
                announceCountdownStatus(ChatColor.RED + "10", ChatColor.GRAY + "The game will start in " + ChatColor.RED + "10s.");
            } else if (secsUntilStart == 5) {
                announceCountdownStatus(ChatColor.RED + "5", ChatColor.GRAY + "The game will start in " + ChatColor.RED + "5s.");
            } else if (secsUntilStart == 3) {
                announceCountdownStatus(ChatColor.GREEN + "3", ChatColor.GRAY + "The game will start in " + ChatColor.GREEN + "3s.");
            } else if (secsUntilStart == 2) {
                announceCountdownStatus(ChatColor.GREEN + "2", ChatColor.GRAY + "The game will start in " + ChatColor.GREEN + "2s.");
            } else if (secsUntilStart == 1) {
                announceCountdownStatus(ChatColor.GREEN + "1", ChatColor.GRAY + "The game will start in " + ChatColor.GREEN + "1s.");
            }

            if (secsUntilStart < 1) {
                announceCountdownStatus(ChatColor.GREEN + "GO", ChatColor.GRAY + "The game has started!");

                // Setup initial game state
                ConfigLoadingModule configLoadingModule = Plugin.getModule(ConfigLoadingModule.class);
                GameStatePlaying state = new GameStatePlaying();

                // Make teams
                List<GameTeam> teams = new ArrayList<>();
                for (Optional<ConfigTeam> configTeam : configLoadingModule.root.getTeams().values()) {
                    configTeam.ifPresent(team -> teams.add(state.makeTeam(team)));
                }

                // Add players to teams
                Iterator<GameTeam> teamPool = null;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Make team pool cycle
                    if (teamPool == null || !teamPool.hasNext()) {
                        teamPool = teams.iterator();
                    }
                    if (!teamPool.hasNext()) return;

                    // Add player to the next team
                    state.addPlayerToTeam(teamPool.next(), player);
                }

                // Start the game
                Plugin.getModule(GameModule.class).setGameState(state);
                state.startRound();
            }
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPluginDisable() { }

    @Override
    public void onStateDisable() { }
}
