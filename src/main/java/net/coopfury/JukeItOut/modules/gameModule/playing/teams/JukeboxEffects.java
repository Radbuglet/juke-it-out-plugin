package net.coopfury.JukeItOut.modules.gameModule.playing.teams;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

class JukeboxEffects {
    // TODO: To avoid wasting memory, all final fields should be stored in a static "config" class.
    public static class EffectLevel {
        public final int effectLevel;
        public final int range;
        public final int cost;

        private EffectLevel(int effectLevel, int range, int cost) {
            this.effectLevel = effectLevel;
            this.range = range;
            this.cost = cost;
        }
    }

    public static class EffectType {
        /**
         * The current level of the effect.
         * -1 means that the effect has never been bought.
         */
        private int currentLevel = -1;

        /**
         * The type of the potion effect.
         */
        public final PotionEffectType effectType;

        /**
         * The icon that displays in the jukebox (lore will be added)
         */
        private final ItemStack icon;

        /**
         * The different upgradable versions of the effect.
         */
        private final EffectLevel[] levels;

        public EffectType(ItemStack icon, PotionEffectType effectType, EffectLevel[] levels) {
            this.icon = icon;
            this.effectType = effectType;
            this.levels = levels;
        }

        public int getCurrentPotency() {
            return currentLevel < 0 ? -1 : levels[currentLevel].effectLevel;
        }

        public Optional<EffectLevel> getNextLevel() {
            return currentLevel + 1 >= levels.length ? Optional.empty() : Optional.of(levels[currentLevel + 1]);
        }
    }

    public final EffectType[] friendlyTypes = new EffectType[]{
            new EffectType(new ItemStack(Material.RABBIT_FOOT), PotionEffectType.SPEED, new EffectLevel[]{
                    new EffectLevel(0, -1, 1),
                    new EffectLevel(1, -1, 1),
                    new EffectLevel(2, -1, 1)
            })
    };

    public final EffectType[] offensiveTypes = new EffectType[]{
            new EffectType(new ItemStack(Material.POISONOUS_POTATO), PotionEffectType.POISON, new EffectLevel[]{
                    new EffectLevel(1, 5, 1),
                    new EffectLevel(2, 10, 1),
                    new EffectLevel(3, 15, 2)
            })
    };
}
