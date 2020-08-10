package net.coopfury.JukeItOut.modules.gameModule.playing.teams;

import net.coopfury.JukeItOut.helpers.gui.InventoryGui;
import net.coopfury.JukeItOut.helpers.spigot.ItemBuilder;
import net.coopfury.JukeItOut.helpers.spigot.PlayerUtils;
import net.coopfury.JukeItOut.helpers.spigot.SpigotEnumConverters;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameStatePlaying;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
         * The potency of this effect if the effect hasn't been bought.
         */
        public final int defaultLevel;

        /**
         * The icon that displays in the jukebox (lore will be added)
         */
        public final ItemStack icon;

        /**
         * The different upgradable versions of the effect.
         */
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
            new EffectType(new ItemStack(Material.RABBIT_FOOT), PotionEffectType.SPEED, -1, new EffectLevel[]{
                    new EffectLevel(0, -1, 1),
                    new EffectLevel(1, -1, 1),
                    new EffectLevel(2, -1, 1)
            })
    };

    private final EffectType[] offensiveTypes = new EffectType[]{
            new EffectType(new ItemStack(Material.POISONOUS_POTATO), PotionEffectType.POISON, -1, new EffectLevel[]{
                    new EffectLevel(1, 5, 1),
                    new EffectLevel(2, 10, 1),
                    new EffectLevel(3, 15, 2)
            })
    };

    private final InventoryGui jukeboxUi;

    // Team management
    public final ConfigTeam configTeam;
    public final List<GameTeamMember> members = new ArrayList<>();

    public GameTeam(ConfigTeam configTeam) {
        this.configTeam = configTeam;
        // TODO: Register inventoryGui to delegator
        jukeboxUi = new InventoryGui("Jukebox", 4);
        jukeboxUi.setItem(jukeboxUi.computeSlot(1, 0), new ItemBuilder(Material.STAINED_GLASS_PANE)
            .setName(ChatColor.GREEN + "Team Effects")
            .setDyeColor(DyeColor.LIME)
            .addLoreLine(ChatColor.GOLD + "Team effects apply throughout the map to your team.")
            .toItemStack());

        jukeboxUi.setItem(jukeboxUi.computeSlot(2, 0), new ItemBuilder(Material.STAINED_GLASS_PANE)
                .setName(ChatColor.RED + "Offensive Effects")
                .setDyeColor(DyeColor.RED)
                .addLoreLine(ChatColor.GOLD + "Offensive effects apply to enemy team members that are in range.")
                .toItemStack());
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

    public void openJukebox(Player player) {
        UiUtils.playSound(player, Sound.CHEST_OPEN);
        jukeboxUi.open(player);
    }
}
