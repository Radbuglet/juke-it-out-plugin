package net.coopfury.JukeItOut.state.game.playing.jukebox;

import net.coopfury.JukeItOut.utils.java.signal.ProcedureSignal;
import net.coopfury.JukeItOut.utils.spigot.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JukeboxEffects {
    // Config types
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
        private int currentLevel = -1;
        private final EffectLevel[] levels;
        public final PotionEffectType effectType;
        public final ItemStack icon;
        public final boolean isOffensive;

        public EffectType(ItemStack icon, PotionEffectType effectType, boolean isOffensive, EffectLevel[] levels) {
            this.icon = icon;
            this.effectType = effectType;
            this.isOffensive = isOffensive;
            this.levels = levels;

            updateIcon();
        }

        public boolean hasBeenUpgraded() {
            return currentLevel > -1;
        }

        public Optional<EffectLevel> getCurrentLevel() {
            return hasBeenUpgraded() ? Optional.of(levels[currentLevel]) : Optional.empty();
        }

        public Optional<EffectLevel> getNextLevel() {
            return currentLevel + 1 >= levels.length ? Optional.empty() : Optional.of(levels[currentLevel + 1]);
        }

        private void updateIcon() {
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(icon.getItemMeta().getDisplayName());

            // Generate lore
            List<String> lore = new ArrayList<>();

            // Add level heading
            {
                // Show information about current level
                lore.add(ChatColor.GRAY + "Current level: " + ChatColor.WHITE + (currentLevel + 1));
                if (isOffensive) {
                    int range = getCurrentLevel().map(level -> level.range).orElse(0);
                    lore.add(ChatColor.GRAY + "Current range: " + ChatColor.WHITE + range + " block" + (range == 1 ? "" : "s"));
                }

                // Show information about next level
                Optional<EffectLevel> nextLevel = getNextLevel();
                if (nextLevel.isPresent()) {
                    lore.add(ChatColor.GRAY + "Cost to upgrade: " + ChatColor.WHITE + nextLevel.get().cost);
                } else {
                    lore.add(ChatColor.RED + "Effect is at its max level!");
                }

                lore.add("");
            }

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

            meta.setLore(lore);

            // Update metadata
            if (hasBeenUpgraded()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeEnchant(Enchantment.DURABILITY);
            }
            icon.setItemMeta(meta);
        }

        public ItemStack getIcon() {
            return icon;
        }
    }

    // Properties
    public final ProcedureSignal onEffectsUpgraded = new ProcedureSignal();
    public final ProcedureSignal onEffectsDowngraded = new ProcedureSignal();
    private int storedDiamonds;

    public final EffectType[] friendlyTypes = new EffectType[]{
            new EffectType(new ItemBuilder(Material.SUGAR).setName(ChatColor.GRAY + "Speed").toItemStack(), PotionEffectType.SPEED, false, new EffectLevel[]{
                    new EffectLevel(0, -1, 1),
                    new EffectLevel(1, -1, 1),
                    new EffectLevel(2, -1, 2)
            }),
            new EffectType(new ItemBuilder(Material.RABBIT_FOOT).setName(ChatColor.GREEN + "Jump Boost").toItemStack(), PotionEffectType.JUMP, false, new EffectLevel[]{
                    new EffectLevel(0, -1, 1),
                    new EffectLevel(1, -1, 1),
                    new EffectLevel(2, -1, 2)
            }),
            new EffectType(new ItemBuilder(Material.SPECKLED_MELON).setName(ChatColor.RED + "Regeneration").toItemStack(), PotionEffectType.REGENERATION, false, new EffectLevel[]{
                    new EffectLevel(0, -1, 2),
                    new EffectLevel(1, -1, 4)
            }),
            new EffectType(new ItemBuilder(Material.GOLDEN_APPLE).setName(ChatColor.YELLOW + "Absorption").toItemStack(), PotionEffectType.ABSORPTION, false, new EffectLevel[]{
                    new EffectLevel(0, -1, 2),
                    new EffectLevel(1, -1, 3),
                    new EffectLevel(2, -1, 3)
            }),
            new EffectType(new ItemBuilder(Material.NETHER_STAR).setName(ChatColor.DARK_RED + "Strength").toItemStack(), PotionEffectType.INCREASE_DAMAGE, false, new EffectLevel[]{
                    new EffectLevel(0, -1, 8)
            }),
            new EffectType(new ItemBuilder(Material.GOLD_PICKAXE).setName(ChatColor.GOLD + "Haste").toItemStack(), PotionEffectType.FAST_DIGGING, false, new EffectLevel[]{
                    new EffectLevel(0, -1, 1),
                    new EffectLevel(1, -1, 1),
                    new EffectLevel(2, -1, 2)
            })
    };

    public final EffectType[] offensiveTypes = new EffectType[]{
            new EffectType(new ItemBuilder(Material.POISONOUS_POTATO).setName(ChatColor.DARK_GREEN + "Poison").toItemStack(), PotionEffectType.POISON, true, new EffectLevel[]{
                    new EffectLevel(1, 8, 2),
                    new EffectLevel(2, 9, 2),
                    new EffectLevel(3, 15, 2)
            }),
            new EffectType(new ItemBuilder(Material.BROWN_MUSHROOM).setName(ChatColor.DARK_BLUE + "Slowness").toItemStack(), PotionEffectType.SLOW, true, new EffectLevel[]{
                    new EffectLevel(1, 8, 1),
                    new EffectLevel(2, 9, 1),
                    new EffectLevel(3, 15, 2)
            }),
            new EffectType(new ItemBuilder(Material.WOOD_PICKAXE).setName(ChatColor.BLUE + "Mining Fatigue").toItemStack(), PotionEffectType.SLOW_DIGGING, true, new EffectLevel[]{
                    new EffectLevel(1, 10, 2),
                    new EffectLevel(2, 15, 3),
                    new EffectLevel(3, 20, 3),
            }),
            new EffectType(new ItemBuilder(Material.SPIDER_EYE).setName(ChatColor.DARK_GRAY + "Blindness").toItemStack(), PotionEffectType.SLOW_DIGGING, true, new EffectLevel[]{
                    new EffectLevel(0, 15, 4)
            })
    };

    public void upgradeEffect(EffectType type) {
        storedDiamonds += type.getNextLevel().orElseThrow(NullPointerException::new).cost;
        type.currentLevel++;
        type.updateIcon();
        onEffectsUpgraded.fire();
    }

    public void downgradeEffect(EffectType type) {
        storedDiamonds -= type.getCurrentLevel().orElseThrow(NullPointerException::new).cost;
        type.currentLevel--;
        type.updateIcon();  // TODO: Update icon correctly
        onEffectsDowngraded.fire();
    }

    public int getStoredDiamonds() {
        return storedDiamonds;
    }

    public void clearDiamonds() {
        if (storedDiamonds == 0) return;
        for (EffectType type : friendlyTypes) {
            type.currentLevel = -1;
            type.updateIcon();
        }
        for (EffectType type : offensiveTypes) {
            type.currentLevel = -1;
            type.updateIcon();
        }
        storedDiamonds = 0;
        onEffectsDowngraded.fire();
    }
}