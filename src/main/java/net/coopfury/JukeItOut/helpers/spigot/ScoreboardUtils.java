package net.coopfury.JukeItOut.helpers.spigot;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class ScoreboardUtils {
    public static Objective obtainObjective(Scoreboard scoreboard, String id, String name, String type) {
        Objective oldObjective = scoreboard.getObjective(id);
        if (oldObjective != null) {
            oldObjective.unregister();
        }

        Objective newObjective = scoreboard.registerNewObjective(id, type);
        newObjective.setDisplayName(name);
        return newObjective;
    }
}
