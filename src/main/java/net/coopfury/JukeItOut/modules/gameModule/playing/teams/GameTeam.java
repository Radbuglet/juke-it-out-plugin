package net.coopfury.JukeItOut.modules.gameModule.playing.teams;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.gui.InventoryGui;
import net.coopfury.JukeItOut.helpers.java.CastUtils;
import net.coopfury.JukeItOut.helpers.spigot.*;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameTeam {
    // Team management
    public final List<GameTeamMember> members = new ArrayList<>();
    public final ConfigTeam configTeam;

    // Game
    private final InventoryGui jukeboxUi;
    private final JukeboxEffects jukeboxEffects = new JukeboxEffects();
    public final Score guiScoreEntry;

    public GameTeam(Objective guiObjective, ConfigTeam configTeam) {
        this.configTeam = configTeam;
        guiScoreEntry = guiObjective.getScore(getTextColor().orElse(ChatColor.GRAY) + configTeam.getName().orElse("Unnamed").toUpperCase());
        guiScoreEntry.setScore(0);

        // Make GUI background
        jukeboxUi = new InventoryGui("Jukebox", 4);
        jukeboxUi.setItem(jukeboxUi.computeSlot(0, 1), new ItemBuilder(Material.STAINED_GLASS_PANE)
            .setName(ChatColor.GREEN + "Team Effects")
            .setDyeColor(DyeColor.LIME)
            .addLoreLine(ChatColor.GOLD + "Team effects apply throughout the map to your team.")
            .toItemStack());

        jukeboxUi.setItem(jukeboxUi.computeSlot(0, 2), new ItemBuilder(Material.STAINED_GLASS_PANE)
                .setName(ChatColor.RED + "Offensive Effects")
                .setDyeColor(DyeColor.RED)
                .addLoreLine(ChatColor.GOLD + "Offensive effects apply to enemy team members that are in range.")
                .toItemStack());

        for (int row = 0; row < 9; row++) {
            ItemStack discStack = new ItemBuilder(new Material[]{
                    Material.RECORD_3,
                    Material.RECORD_4,
                    Material.RECORD_5,
                    Material.RECORD_6,
                    Material.RECORD_7,
                    Material.RECORD_8,
                    Material.RECORD_9,
                    Material.RECORD_10,
                    Material.RECORD_11
            }[row]).setName(" ").toItemStack();

            jukeboxUi.setItem(jukeboxUi.computeSlot(row, 0), discStack);
            jukeboxUi.setItem(jukeboxUi.computeSlot(row, 3), discStack.clone());
        }

        // Make GUI options
        populateEffectRow(jukeboxEffects.friendlyTypes, 1, true);
        populateEffectRow(jukeboxEffects.offensiveTypes, 2, false);

        // Register GUI
        Plugin.inventoryGui.registerMenu(jukeboxUi);
    }

    public void addMember(TeamManager teamManager, UUID playerUuid) {
        GameTeamMember member = new GameTeamMember(this, playerUuid);
        teamManager.internalRegisterMember(member);
        members.add(member);
    }

    void internalUnregisterMember(GameTeamMember member) {
        members.remove(member);
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
    private void populateEffectRow(JukeboxEffects.EffectType[] types, int column, boolean isFriendly) {
        int slotOffset = column * 9;
        int row = 1;
        for (JukeboxEffects.EffectType type: types) {
            jukeboxUi.setItem(slotOffset + row, type.renderIcon(!isFriendly), event -> {
                event.setCancelled(true);
                HumanEntity player = event.getWhoClicked();

                // Perform the relevant action
                if (event.getAction() == InventoryAction.PICKUP_ALL) {
                    // Check purchase
                    Optional<JukeboxEffects.EffectLevel> nextLevel = type.getNextLevel();
                    if (!nextLevel.isPresent()) {
                        player.sendMessage(ChatColor.RED + "That effect is already at its max level!");
                        return;
                    }

                    if (!InventoryUtils.tryPurchase(player.getInventory(), stack -> stack.getType() == Material.DIAMOND, nextLevel.get().cost))
                    {
                        player.sendMessage(ChatColor.RED + "Not enough diamonds to purchase that upgrade!");
                        return;
                    }

                    // Increase the level
                    jukeboxEffects.upgradeEffect(type);
                } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                    Optional<JukeboxEffects.EffectLevel> currentLevel = type.getCurrentLevel();

                    if (!currentLevel.isPresent()) {
                        player.sendMessage(ChatColor.RED + "That effect is already at its lowest level!");
                        return;
                    }

                    player.getInventory().addItem(new ItemStack(Material.DIAMOND, currentLevel.get().cost));

                    // Decrease the level
                    jukeboxEffects.downgradeEffect(type);
                } else {
                    return;
                }

                // Reapply team effects (enemy effects will be applied every tick anyways)
                if (isFriendly) applyFriendlyEffects();

                // Rerender icon
                ItemStack stack = event.getCurrentItem();
                type.renderIcon(stack, !isFriendly);
                UiUtils.playSound((Player) player, Sound.NOTE_PLING);  // Player is the only subclass of HumanEntity.
                updateDiamondScoreGui();
            });
            row++;
        }
    }

    public void openJukebox(Player player) {
        UiUtils.playSound(player, Sound.CHEST_OPEN);
        jukeboxUi.open(player);
    }

    // Jukebox effects
    public void applyFriendlyEffects() {
        for (GameTeamMember member: members) {
            Player player = member.getPlayer();
            PlayerUtils.resetPlayerEffects(player);
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

            for (GameTeamMember member : otherTeam.members) {
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
        Plugin.inventoryGui.unregisterMenu(jukeboxUi);
    }
}
