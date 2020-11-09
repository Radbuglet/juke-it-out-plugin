package net.coopfury.JukeItOut.helpers.game;

import java.util.*;

public abstract class AbstractTeam<TMember> {
    BaseTeamManager<?, TMember> manager;
    final Map<UUID, TMember> members = new HashMap<>();

    public Optional<TMember> getMember(UUID uuid) {
        return Optional.ofNullable(members.get(uuid));
    }

    public Collection<TMember> getMembers() {
        return members.values();
    }

    public Set<Map.Entry<UUID, TMember>> getMemberEntries() {
        return members.entrySet();
    }
}
