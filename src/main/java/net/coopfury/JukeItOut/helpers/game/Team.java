package net.coopfury.JukeItOut.helpers.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class Team<TMember> {
    TeamManager<?, TMember> manager;
    final Map<UUID, TMember> members = new HashMap<>();

    public Optional<TMember> getPlayer(UUID uuid) {
        return Optional.ofNullable(members.get(uuid));
    }
}
