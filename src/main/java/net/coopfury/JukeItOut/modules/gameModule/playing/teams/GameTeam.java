package net.coopfury.JukeItOut.modules.gameModule.playing.teams;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.gui.InventoryGui;
import net.coopfury.JukeItOut.helpers.spigot.*;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameTeam {
    // Jukebox
    private final InventoryGui jukeboxUi;
    private final JukeboxEffects jukeboxEffects = new JukeboxEffects();

    // Team management
    public final ConfigTeam configTeam;
    public final List<GameTeamMember> members = new ArrayList<>();

    public GameTeam(ConfigTeam configTeam) {
        this.configTeam = configTeam;

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

    private void populateEffectRow(JukeboxEffects.EffectType[] types, int column, boolean isFriendly) {
        int slotOffset = column * 9;
        int row = 1;
        for (JukeboxEffects.EffectType type: types) {
            jukeboxUi.setItem(slotOffset + row, type.renderIcon(), event -> {
                event.setCancelled(true);
                HumanEntity player = event.getWhoClicked();

                if (event.getAction() == InventoryAction.PICKUP_ALL) {
                    // Check purchase
                    Optional<JukeboxEffects.EffectLevel> nextLevel = type.getNextLevel();
                    if (!nextLevel.isPresent()) {
                        player.sendMessage(ChatColor.RED + "That effect is already at its max level!");
                        player.closeInventory();
                        return;
                    }

                    if (!InventoryUtils.tryPurchase(player.getInventory(), stack -> stack.getType() == Material.DIAMOND, nextLevel.get().cost))
                    {
                        player.sendMessage(ChatColor.RED + "Not enough diamonds to purchase that upgrade!");
                        player.closeInventory();
                        return;
                    }

                    // Rerender
                    ItemStack stack = event.getCurrentItem();
                    type.currentLevel++;
                    type.renderIcon(stack);

                    // Reapply team effects (enemy effects will be applied every tick anyways)
                    if (isFriendly) applyFriendlyEffects();
                } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                    // TODO: Down-grade
                }
            });
            row++;
        }
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
            for (JukeboxEffects.EffectType type: jukeboxEffects.friendlyTypes) {
                PlayerUtils.setEffectLevel(player, type.effectType, type.getCurrentPotency());
            }
        }
    }

    void applyOffensiveEffects() {
        // TODO
    }

    public void openJukebox(Player player) {
        UiUtils.playSound(player, Sound.CHEST_OPEN);
        jukeboxUi.open(player);
    }

    public void onGameStateChange() {
        Plugin.inventoryGui.unregisterMenu(jukeboxUi);
    }
}
