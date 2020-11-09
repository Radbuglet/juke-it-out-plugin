package net.coopfury.JukeItOut.helpers.game;

public final class MemberPair<TTeam extends AbstractTeam<TMember>, TMember> {
    public final TTeam team;
    public final TMember member;

    public MemberPair(TTeam team, TMember member) {
        this.team = team;
        this.member = member;
    }
}