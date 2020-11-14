package net.coopfury.JukeItOut.modules.gameModule.playing.teams;

import net.coopfury.JukeItOut.helpers.game.AbstractTeam;
import net.coopfury.JukeItOut.helpers.java.CastUtils;
import net.coopfury.JukeItOut.helpers.spigot.PlayerUtils;
import net.coopfury.JukeItOut.helpers.spigot.SpigotEnumConverters;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import net.coopfury.JukeItOut.modules.gameModule.playing.jukebox.JukeboxEffects;
import net.coopfury.JukeItOut.modules.gameModule.playing.jukebox.JukeboxGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.Optional;

public class GameTeam extends AbstractTeam<GameTeamMember> {
    public final ConfigTeam configTeam;
    private final JukeboxEffects jukeboxEffects = new JukeboxEffects();
    private final JukeboxGui jukeboxGui = new JukeboxGui(jukeboxEffects);
    public final Score guiScoreEntry;

    public GameTeam(Objective guiObjective, ConfigTeam configTeam) {
        this.configTeam = configTeam;

        // Setup scoreboard
        guiScoreEntry = guiObjective.getScore(getTextColor().orElse(ChatColor.GRAY) + configTeam.getName().orElse("Unnamed").toUpperCase());
        guiScoreEntry.setScore(0);

        // Setup jukebox
        jukeboxEffects.effectUpgradedListener = type -> {  // TODO: Some of these re-applications are unnecessary.
            this.applyFriendlyEffects();
            this.updateDiamondScoreGui();
        };
        jukeboxEffects.effectDowngradedListener = type -> {
            this.applyFriendlyEffects();
            this.updateDiamondScoreGui();
        };
        jukeboxGui.registerGui();
    }

    // Config aliases
    public Optional<ChatColor> getTextColor() {
        return configTeam.getWoolColor().flatMap(SpigotEnumConverters.DYE_TO_CHAT::parse);
    }

    public Optional<Chest> getTeamChest() {
        return configTeam.getChestLocation()  // Get location
                .map(Location::getBlock)  // Get block
                .flatMap(block -> CastUtils.dynamicCast(Chest.class, block.getState()));  // Get state
    }

    // Jukebox UI
    public void openJukebox(Player player) {
        jukeboxGui.open(player);
    }

    // Jukebox effects
    public void applyFriendlyEffects() {
        for (GameTeamMember member: getMembers()) {
            Player player = member.getPlayer();
            PlayerUtils.resetEffects(player);
            for (JukeboxEffects.EffectType type: jukeboxEffects.friendlyTypes) {
                PlayerUtils.setEffectLevel(player, type.effectType, type.getCurrentLevel().map(level -> level.effectLevel).orElse(-1));
            }
        }
    }

    public void applyOffensiveEffects(TeamManager manager) {
        Optional<Location> jukeboxLocation = configTeam.getJukeboxLocation();
        if (!jukeboxLocation.isPresent()) return;

        for (GameTeam otherTeam : manager.getTeams()) {
            if (otherTeam == this) continue;

            for (GameTeamMember member : otherTeam.getMembers()) {
                if (!member.isAlive) continue;

                Player player = member.getPlayer();
                double distance = player.getLocation().distance(jukeboxLocation.get());

                for (JukeboxEffects.EffectType type : jukeboxEffects.offensiveTypes) {
                    Optional<JukeboxEffects.EffectLevel> level = type.getCurrentLevel();
                    if (!level.isPresent()) continue;
                    if (distance <= level.get().range) {
                        player.addPotionEffect(new PotionEffect(type.effectType, 20 * 2, level.get().effectLevel));
                    }
                }
            }
        }
    }

    // Diamond counting
    public int getTeamDiamondCount() {
        int accumulator = jukeboxEffects.getStoredDiamonds();

        Optional<Chest> teamChest = getTeamChest();
        if (teamChest.isPresent()) {
            Inventory inventory = teamChest.get().getBlockInventory();
            for (ItemStack stack: inventory.getContents()) {
                if (stack == null || stack.getType() != Material.DIAMOND) continue;
                accumulator += stack.getAmount();
            }
        }

        return accumulator;
    }

    public void updateDiamondScoreGui() {
        guiScoreEntry.setScore(getTeamDiamondCount());
    }

    // Resetting
    public void onStateDisable() {
        jukeboxGui.unregisterGui();
    }
}
