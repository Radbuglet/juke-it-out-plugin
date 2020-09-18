package net.coopfury.JukeItOut.modules.gameModule.playing.teams;

import net.coopfury.JukeItOut.helpers.spigot.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        public int currentLevel = -1;

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

        public Optional<EffectLevel> getCurrentLevel() {
            return currentLevel > -1 ? Optional.of(levels[currentLevel]) : Optional.empty();
        }

        public int getCurrentPotency() {
            return getCurrentLevel().map(level -> level.effectLevel).orElse(-1);
        }

        public Optional<EffectLevel> getNextLevel() {
            return currentLevel + 1 >= levels.length ? Optional.empty() : Optional.of(levels[currentLevel + 1]);
        }

        public ItemStack renderIcon(ItemStack target) {
            ItemMeta meta = target.getItemMeta();
            meta.setDisplayName(icon.getItemMeta().getDisplayName());

            // Generate lore
            List<String> lore = new ArrayList<>();

            // Add level heading
            lore.add(ChatColor.GRAY + "Current level: " + ChatColor.WHITE + (currentLevel + 1));
            {
                Optional<EffectLevel> nextLevel = getNextLevel();
                if (nextLevel.isPresent()) {
                    lore.add(ChatColor.GRAY + "Cost to upgrade: " + ChatColor.WHITE + nextLevel.get().cost);
                } else {
                    lore.add(ChatColor.RED + "Effect is at its max level!");
                }
            }
            lore.add("");

            // Add levels
            {
                int levelNumber = 0;
                for (EffectLevel level : levels) {
                    lore.add((currentLevel == levelNumber ? ChatColor.AQUA + "> " : ChatColor.BLUE + "  ") +
                            (levelNumber + 1) + ChatColor.GRAY + " - " + ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + level.cost);
                    levelNumber++;
                }
            }
            lore.add("");
            lore.add(ChatColor.GREEN + "Left click to upgrade");
            lore.add(ChatColor.GREEN + "Right click to downgrade");

            // Set lore
            meta.setLore(lore);
            target.setItemMeta(meta);
            return target;
        }

        public ItemStack renderIcon() {
            return renderIcon(icon.clone());
        }
    }

    public final EffectType[] friendlyTypes = new EffectType[]{
            new EffectType(new ItemBuilder(Material.RABBIT_FOOT).setName(ChatColor.GREEN + "Speed").toItemStack(), PotionEffectType.SPEED, new EffectLevel[]{
                    new EffectLevel(0, -1, 1),
                    new EffectLevel(1, -1, 1),
                    new EffectLevel(2, -1, 1)
            })
    };

    public final EffectType[] offensiveTypes = new EffectType[]{
            new EffectType(new ItemBuilder(Material.POISONOUS_POTATO).setName(ChatColor.DARK_GREEN + "Poison").toItemStack(), PotionEffectType.POISON, new EffectLevel[]{
                    new EffectLevel(1, 5, 1),
                    new EffectLevel(2, 10, 1),
                    new EffectLevel(3, 15, 2)
            })
    };
}
