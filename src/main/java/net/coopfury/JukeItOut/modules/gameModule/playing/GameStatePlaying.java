package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.java.TimeUnits;
import net.coopfury.JukeItOut.helpers.java.TimestampUtils;
import net.coopfury.JukeItOut.helpers.spigot.ItemBuilder;
import net.coopfury.JukeItOut.helpers.spigot.PlayerUtils;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.GameState;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GameStatePlaying implements GameState {
    private final List<GameTeam> teams = new ArrayList<>();
    private final Map<UUID, GameTeamMember> memberMap = new HashMap<>();
    private int roundId;
    private long roundEndTime;

    public void startRound() {
        roundId++;
        roundEndTime = TimestampUtils.getTimeIn(TimeUnits.Secs, 20);
        for (GameTeam team: teams) {
            for (GameTeamMember member: team.members) {
                Player player = member.getPlayer();
                PlayerUtils.resetPlayer(player);

                Inventory inventory = player.getInventory();
                inventory.addItem(new ItemStack(Material.IRON_SWORD));
                inventory.addItem(new ItemBuilder(Material.STAINED_CLAY, 64).setDyeColor(DyeColor.GREEN).toItemStack());

                player.teleport(team.configTeam.getSpawnLocation().orElse(null));
                UiUtils.playTitle(player, String.format(ChatColor.RED + "Round %s", roundId), Constants.title_timings_important);
                UiUtils.playSound(player, Sound.ENDERDRAGON_HIT);
            }
        }
    }

    @Override
    public void tick() {
        long timeUntilRoundEnd = TimestampUtils.getTimeUntil(roundEndTime, TimeUnits.Ms);
        if (timeUntilRoundEnd < 0) {
            startRound();
            return;
        }
        for (GameTeamMember member: memberMap.values()) {
            Player player = member.getPlayer();  // Runnables are executed before any player disconnection handling occurs
            player.setExp((float) (timeUntilRoundEnd % 1000) / 1000);
            player.setLevel((int) (timeUntilRoundEnd / 1000));
        }
    }

    public GameTeam makeTeam(ConfigTeam teamConfig) {
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
