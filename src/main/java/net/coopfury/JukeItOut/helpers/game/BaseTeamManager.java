package net.coopfury.JukeItOut.helpers.game;

import java.util.*;

public class BaseTeamManager<TTeam extends AbstractTeam<TMember>, TMember> {
    private final Set<TTeam> teams = new HashSet<>();
    private final Map<UUID, TTeam> memberTeams = new HashMap<>();

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
        teams.add(team);
        for (UUID uuid: team.members.keySet()) {
            memberTeams.put(uuid, team);
        }
    }

    public boolean removeTeam(AbstractTeam<?> team) {
        if (team.manager != this) {
            return false;
        }

        for (UUID uuid: team.members.keySet()) {
            memberTeams.remove(uuid);
        }
        //noinspection SuspiciousMethodCalls
        teams.remove(team);
        team.manager = null;
        return true;
    }

    public void addMemberInto(UUID uuid, TTeam team, TMember newMember) {
        if (team.manager != this) {
            throw new IllegalArgumentException("Cannot move player into an unregistered team!");
        }

        // Replace the old member with the new member
        TTeam oldTeam = memberTeams.put(uuid, team);
        if (oldTeam != null && oldTeam != team) {
            oldTeam.members.remove(uuid);
        }

        // Register that new member in their team
        team.members.put(uuid, newMember);
    }

    public void moveMemberInto(UUID uuid, TTeam team) {
        if (team.manager != this) {
            throw new IllegalArgumentException("Cannot move player into an unregistered team!");
        }
        // Remove player from their old team
        TTeam oldTeam = getMemberTeam(uuid).orElseThrow(IllegalAccessError::new);
        TMember member = oldTeam.members.get(uuid);
        oldTeam.members.remove(uuid);

        // Add them to the new team
        memberTeams.put(uuid, team);
        team.members.put(uuid, member);
    }

    public boolean replaceMember(UUID uuid, TMember newMember) {
        TTeam team = memberTeams.get(uuid);
        if (team == null) {
            return false;
        }

        team.members.put(uuid, newMember);
        return true;
    }

    public boolean removeMember(UUID uuid) {
        Optional<TTeam> team = getMemberTeam(uuid);
        if (!team.isPresent()) {
            return false;
        }

        team.get().members.remove(uuid);
        memberTeams.remove(uuid);
        return true;
    }

    public Optional<TTeam> getMemberTeam(UUID uuid) {
        return Optional.ofNullable(memberTeams.get(uuid));
    }

    public Optional<TMember> getMember(UUID uuid) {
        return getMemberTeam(uuid).flatMap(team -> team.getMember(uuid));
    }

    public Optional<MemberPair<TTeam, TMember>> getMemberPair(UUID uuid) {
        TTeam team = memberTeams.get(uuid);
        return team != null ? Optional.of(new MemberPair<>(
                team, team.getMember(uuid).orElseThrow(IllegalStateException::new)
        )) : Optional.empty();
    }

    public Set<UUID> getMemberUuids() {
        return memberTeams.keySet();
    }

    public Collection<TMember> getMembers() {
        return new AbstractCollection<TMember>() {
            @Override
            public Iterator<TMember> iterator() {
                return new Iterator<TMember>() {
                    private final Iterator<UUID> uuidIterator = getMemberUuids().iterator();

                    @Override
                    public boolean hasNext() {
                        return uuidIterator.hasNext();
                    }

                    @Override
                    public TMember next() {
                        return getMember(uuidIterator.next()).orElseThrow(IllegalStateException::new);
                    }
                };
            }

            @Override
            public int size() {
                return memberTeams.size();
            }
        };
    }

    public Collection<TTeam> getTeams() {
        return teams;
    }
}
