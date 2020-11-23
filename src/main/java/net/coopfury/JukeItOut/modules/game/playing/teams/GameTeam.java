package net.coopfury.JukeItOut.modules.game.playing.teams;

import net.coopfury.JukeItOut.helpers.game.AbstractTeam;
import net.coopfury.JukeItOut.modules.config.ConfigTeam;

public class GameTeam extends AbstractTeam<GameMember> {
    public final ConfigTeam configTeam;

    public GameTeam(ConfigTeam configTeam) {
        this.configTeam = configTeam;
    }
}
