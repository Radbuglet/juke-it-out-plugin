package net.coopfury.JukeItOut.modules.gameModule.playing.teams;

import net.coopfury.JukeItOut.helpers.spigot.PlayerUtils;
import net.coopfury.JukeItOut.helpers.spigot.SpigotEnumConverters;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameStatePlaying;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GameTeam {
    // Jukebox effect declaration
    private static class EffectLevel {
        public final int effectLevel;
        public final int range;
        public final int cost;

        private EffectLevel(int effectLevel, int range, int cost) {
            this.effectLevel = effectLevel;
            this.range = range;
            this.cost = cost;
        }
    }

    private static class EffectType {
        public int currentLevel;
        public final PotionEffectType effectType;
        public final int defaultLevel;
        public final ItemStack icon;
        public final EffectLevel[] levels;

        public EffectType(ItemStack icon, PotionEffectType effectType, int defaultLevel, EffectLevel[] levels) {
            this.icon = icon;
            this.defaultLevel = defaultLevel;
            this.effectType = effectType;
            this.levels = levels;
        }

        public int getCurrentPotency() {
            return currentLevel < 0 ? defaultLevel : levels[currentLevel].effectLevel;
        }
    }

    private final EffectType[] friendlyTypes = new EffectType[]{
            new EffectType(new ItemStack(Material.RABBIT_FOOT), PotionEffectType.SPEED, 1, new EffectLevel[]{
                    new EffectLevel(2, -1, 1),
                    new EffectLevel(3, -1, 1),
                    new EffectLevel(4, -1, 1)
            })
    };
    private final EffectType[] offensiveTypes = new EffectType[]{
            new EffectType(new ItemStack(Material.POISONOUS_POTATO), PotionEffectType.POISON, 0, new EffectLevel[]{
                    new EffectLevel(1, 5, 1),
                    new EffectLevel(2, 10, 1),
                    new EffectLevel(3, 15, 2)
            })
    };

    // Team management
    public final ConfigTeam configTeam;
    public final List<GameTeamMember> members = new ArrayList<>();

    public GameTeam(ConfigTeam configTeam) {
        this.configTeam = configTeam;
    }

    public GameTeamMember addMember(TeamManager teamManager, UUID playerUuid) {
        GameTeamMember member = new GameTeamMember(this, playerUuid);
        teamManager.internalRegisterMember(member);
        members.add(member);
        return member;
    }

    void internalUnregisterMember(GameTeamMember member) {
        members.remove(member);
    }

    // Text formatting
    public Optional<ChatColor> getTextColor() {
        return configTeam.getWoolColor().flatMap(SpigotEnumConverters.DYE_TO_CHAT::parse);
    }

    // Jukebox management
    public void applyFriendlyEffects() {
        for (GameTeamMember member: members) {
            Player player = member.getPlayer();
            PlayerUtils.resetPlayerEffects(player);
            for (EffectType type: friendlyTypes) {
                PlayerUtils.setEffectLevel(player, type.effectType, type.getCurrentPotency());
            }
        }
    }

    void reapplyOffensiveEffects() {
        // TODO
    }
}
