package net.coopfury.JukeItOut.helpers.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class TeamManager<TTeam extends Team<TMember>, TMember> {
    private final Map<UUID, TTeam> memberTeams = new HashMap<>();
    private final Supplier<TMember> memberFactory;

    public TeamManager(Supplier<TMember> memberFactory) {
        this.memberFactory = memberFactory;
    }

    public void addTeam(TTeam team) {
        // Ensure that the team is contained in a correct manager (either this or null)
        if (team.manager != this) {
            if (team.manager != null) {
                team.manager.removeTeam(team);
            }
        } else {
            return;
        }

        // Add the orphan team into the manager.
        team.manager = this;
        for (UUID uuid: team.members.keySet()) {
            memberTeams.put(uuid, team);
        }
    }

    public boolean removeTeam(Team<?> team) {
        if (team.manager != this) {
            return false;
        }

        for (UUID uuid: team.members.keySet()) {
            memberTeams.remove(uuid);
        }
        team.manager = null;
        return true;
    }

    public void addPlayerInto(UUID uuid, TTeam newTeam) {
        // Replace the old member with the new member
        TTeam oldTeam = memberTeams.put(uuid, newTeam);
        if (oldTeam != null && oldTeam != newTeam) {
            oldTeam.members.remove(uuid);
        }

        // Register that new member in their team
        newTeam.members.put(uuid, memberFactory.get());
    }

    public void movePlayerInto(UUID uuid, TTeam team) {
        // Remove player from their old team
        TTeam oldTeam = getPlayerTeam(uuid).orElseThrow(IllegalAccessError::new);
        TMember member = oldTeam.members.get(uuid);
        oldTeam.members.remove(uuid);

        // Add them to the new team
        memberTeams.put(uuid, team);
        team.members.put(uuid, member);
    }

    public boolean replacePlayer(UUID uuid, TMember newMember) {
        TTeam team = memberTeams.get(uuid);
        if (team == null) {
            return false;
        }

        team.members.put(uuid, newMember);
        return true;
    }

    public boolean removePlayer(UUID uuid) {
        Optional<TTeam> team = getPlayerTeam(uuid);
        if (!team.isPresent()) {
            return false;
        }

        team.get().members.remove(uuid);
        memberTeams.remove(uuid);
        return true;
    }

    public Optional<TTeam> getPlayerTeam(UUID uuid) {
        return Optional.ofNullable(memberTeams.get(uuid));
    }

    public Optional<TMember> getPlayer(UUID uuid) {
        return getPlayerTeam(uuid).flatMap(team -> team.getPlayer(uuid));
    }
}
