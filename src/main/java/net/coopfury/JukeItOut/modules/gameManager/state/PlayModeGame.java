package net.coopfury.JukeItOut.modules.gameManager.state;

import net.coopfury.JukeItOut.Game;
import net.coopfury.JukeItOut.helpers.java.TimeUnits;
import net.coopfury.JukeItOut.helpers.java.TimestampUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayModeGame implements PlayModeCommon {
    List<PlayTeam> teams = new ArrayList<>();
    int round;
    long roundEndsAt;

    public void startRound() {
        round++;
        roundEndsAt = TimestampUtils.getTimeIn(TimeUnits.Secs, 30);

        for (PlayTeam team: teams) {
            team.teleportToSpawnRoundBegin(this);
        }

        for (World world: Bukkit.getWorlds()) {
            for (Item item: world.getEntitiesByClass(Item.class)) {
                item.remove();
            }
        }
    }

    public void tick() {
        for (Player player: Game.getGame().getServer().getOnlinePlayers()) {
            player.sendMessage("Round ends at " + TimestampUtils.getTimeUntil(roundEndsAt, TimeUnits.Secs));
        }
    }
}
