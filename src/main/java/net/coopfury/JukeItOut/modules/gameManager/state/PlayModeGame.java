package net.coopfury.JukeItOut.modules.gameManager.state;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.List;

public class PlayModeGame {
    List<PlayTeam> teams = new ArrayList<>();
    int round;
    int roundTime;

    public void startRound() {
        round++;
        roundTime = 60;

        for (PlayTeam team: teams) {
            team.teleportToSpawnRoundBegin(this);
        }

        for (World world: Bukkit.getWorlds()) {
            for (Item item: world.getEntitiesByClass(Item.class)) {
                item.remove();
            }
        }
    }
}
