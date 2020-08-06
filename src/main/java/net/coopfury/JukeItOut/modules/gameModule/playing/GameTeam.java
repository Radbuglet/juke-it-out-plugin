package net.coopfury.JukeItOut.modules.gameModule.playing;

import net.coopfury.JukeItOut.helpers.spigot.SpigotEnumConverters;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameTeam {
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
        public final ItemStack icon;
        public final EffectLevel[] levels;

        public EffectType(ItemStack icon, PotionEffectType effectType, EffectLevel[] levels) {
            this.icon = icon;
            this.effectType = effectType;
            this.levels = levels;
        }
    }

    public final ConfigTeam configTeam;
    public final List<GameTeamMember> members = new ArrayList<>();

    private final EffectType[] friendlyTypes = new EffectType[]{
        new EffectType(new ItemStack(Material.RABBIT_FOOT), PotionEffectType.SPEED, new EffectLevel[]{
                new EffectLevel(1, -1, 1),
                new EffectLevel(2, -1, 1),
                new EffectLevel(3, -1, 1)
        })
    };
    private final EffectType[] offensiveTypes = new EffectType[]{
            new EffectType(new ItemStack(Material.POISONOUS_POTATO), PotionEffectType.POISON, new EffectLevel[]{
                    new EffectLevel(1, 5, 1),
                    new EffectLevel(2, 10, 1),
                    new EffectLevel(3, 15, 2)
            })
    };

    public GameTeam(ConfigTeam configTeam) {
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

    public Optional<ChatColor> getTextColor() {
        return configTeam.getWoolColor().flatMap(SpigotEnumConverters.DYE_TO_CHAT::parse);
    }
}
