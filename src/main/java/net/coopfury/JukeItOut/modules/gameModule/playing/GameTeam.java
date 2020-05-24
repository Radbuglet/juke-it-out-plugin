package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.modules.configLoading.SchemaTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameTeam {
    public final SchemaTeam configTeam;
    public final List<GameTeamMember> members = new ArrayList<>();

    public GameTeam(SchemaTeam configTeam) {
        this.configTeam = configTeam;
    }

    public GameTeamMember addMember(GameStatePlaying playingState, UUID playerUuid) {
        GameTeamMember member = new GameTeamMember(this, playerUuid);
        playingState.registerMember(member);
        members.add(member);
        return member;
    }

    void unregisterMember(GameTeamMember member) {
        members.remove(member);
    }
}
