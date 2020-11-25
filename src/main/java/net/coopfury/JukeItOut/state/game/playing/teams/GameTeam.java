package net.coopfury.JukeItOut.state.game.playing.teams;

import net.coopfury.JukeItOut.state.game.playing.jukebox.JukeboxEffects;
import net.coopfury.JukeItOut.state.game.playing.jukebox.JukeboxGui;
import net.coopfury.JukeItOut.utils.game.AbstractTeam;
import net.coopfury.JukeItOut.utils.java.CastUtils;
import net.coopfury.JukeItOut.utils.java.signal.SignalPriority;
import net.coopfury.JukeItOut.utils.spigot.PlayerUtils;
import net.coopfury.JukeItOut.utils.spigot.SpigotEnumConverters;
import net.coopfury.JukeItOut.state.config.ConfigTeam;
import net.coopfury.JukeItOut.utils.spigot.UiUtils;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.Optional;

public class GameTeam extends AbstractTeam<GameMember> {
    public final ConfigTeam configTeam;
    private final JukeboxEffects jukeboxEffects = new JukeboxEffects();
    private final JukeboxGui jukeboxGui = new JukeboxGui(jukeboxEffects);
    public final Score guiScoreEntry;

    public GameTeam(ConfigTeam configTeam, Objective guiObjective) {
        this.configTeam = configTeam;

        // Setup scoreboard
        guiScoreEntry = guiObjective.getScore(getTextColor().orElse(ChatColor.GRAY) + configTeam.getName().orElse("Unnamed").toUpperCase());
        guiScoreEntry.setScore(0);

        // Setup jukebox
        jukeboxEffects.onEffectsUpgraded.connect(this::onEffectLevelChanged, SignalPriority.Medium);
        jukeboxEffects.onEffectsDowngraded.connect(this::onEffectLevelChanged, SignalPriority.Medium);
        jukeboxGui.registerGui();
    }

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
        UiUtils.playSound(player, Sound.CHEST_OPEN);
    }

    public void stealDiamonds(Player player) {
        int diamondCount = jukeboxEffects.getStoredDiamonds();
        if (diamondCount > 0) {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, diamondCount));
            jukeboxEffects.clearDiamonds();
            player.sendMessage(ChatColor.BLUE + "You stole " + ChatColor.AQUA + diamondCount + " diamond" + (diamondCount == 1 ? "" : "s") + ".");
            UiUtils.playSound(player, Sound.ITEM_PICKUP);
        } else {
            player.sendMessage(ChatColor.BLUE + "This jukebox is empty!");
            UiUtils.playSound(player, Sound.ITEM_BREAK);
        }
        player.getWorld().playEffect(player.getLocation(), Effect.MAGIC_CRIT, null);

    }

    // Jukebox effects
    private void onEffectLevelChanged() {
        applyFriendlyEffects();
        updateDiamondScoreGui();
    }

    public void applyFriendlyEffects() {
        for (GameMember member: getMembers()) {
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

            for (GameMember member : otherTeam.getMembers()) {
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

    private void updateDiamondScoreGui() {
        guiScoreEntry.setScore(getTeamDiamondCount());
    }

    // Event handlers
    public void tick(TeamManager teamManager) {
        updateDiamondScoreGui();
        applyOffensiveEffects(teamManager);
    }

    public void cleanup() {
        jukeboxGui.unregisterGui();
    }
}
