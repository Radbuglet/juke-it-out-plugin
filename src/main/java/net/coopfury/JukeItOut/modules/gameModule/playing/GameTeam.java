package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.modules.configLoading.SchemaTeam;

import java.util.ArrayList;
import java.util.List;

public class GameTeam {
    public final SchemaTeam configTeam;
    public final List<GameTeamMember> members = new ArrayList<>();

    public GameTeam(SchemaTeam configTeam) {
        this.configTeam = configTeam;
    }

    public void addMember(GameStatePlaying playingState, GameTeamMember member) {
        playingState.registerMember(member);
        members.add(member);
    }
}
