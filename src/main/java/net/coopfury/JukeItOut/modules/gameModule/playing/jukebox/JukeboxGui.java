package net.coopfury.JukeItOut.modules.gameModule.playing.jukebox;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.gui.InventoryGui;
import net.coopfury.JukeItOut.helpers.spigot.ItemBuilder;
import net.coopfury.JukeItOut.helpers.spigot.PlayerUtils;
import net.coopfury.JukeItOut.helpers.spigot.UiUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class JukeboxGui {
    private final InventoryGui gui = new InventoryGui("Jukebox", 4);
    private final JukeboxEffects effects;

    public JukeboxGui(JukeboxEffects effects) {
        this.effects = effects;

        gui.setItem(gui.computeSlot(0, 1), new ItemBuilder(Material.STAINED_GLASS_PANE)
                .setName(ChatColor.GREEN + "Team Effects")
                .setDyeColor(DyeColor.LIME)
                .addLoreLine(ChatColor.GOLD + "Team effects apply throughout the map to your team.")
                .toItemStack());

        gui.setItem(gui.computeSlot(0, 2), new ItemBuilder(Material.STAINED_GLASS_PANE)
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

            gui.setItem(gui.computeSlot(row, 0), discStack);
            gui.setItem(gui.computeSlot(row, 3), discStack.clone());
        }

        populateEffectRow(effects.friendlyTypes, 1, true);
        populateEffectRow(effects.offensiveTypes, 2, false);
    }

    private void populateEffectRow(JukeboxEffects.EffectType[] types, int column, boolean isFriendly) {
        int slotOffset = column * 9;
        int row = 1;
        for (JukeboxEffects.EffectType type: types) {
            gui.setItem(slotOffset + row, type.renderIcon(!isFriendly), event -> {
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

                    if (!PlayerUtils.tryPurchase(player.getInventory(), stack -> stack.getType() == Material.DIAMOND, nextLevel.get().cost))
                    {
                        player.sendMessage(ChatColor.RED + "Not enough diamonds to purchase that upgrade!");
                        return;
                    }

                    // Increase the level
                    effects.upgradeEffect(type);
                } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                    Optional<JukeboxEffects.EffectLevel> currentLevel = type.getCurrentLevel();

                    if (!currentLevel.isPresent()) {
                        player.sendMessage(ChatColor.RED + "That effect is already at its lowest level!");
                        return;
                    }

                    player.getInventory().addItem(new ItemStack(Material.DIAMOND, currentLevel.get().cost));

                    // Decrease the level
                    effects.downgradeEffect(type);
                } else {
                    return;
                }

                // Rerender icon
                ItemStack stack = event.getCurrentItem();
                type.renderIcon(stack, !isFriendly);
                UiUtils.playSound((Player) player, Sound.NOTE_PLING);  // Player is the only subclass of HumanEntity.
            });
            row++;
        }
    }

    public void open(Player player) {
        UiUtils.playSound(player, Sound.CHEST_OPEN);
        gui.open(player);
    }

    public void registerGui() {
        Plugin.inventoryGui.registerMenu(gui);
    }

    public void unregisterGui() {
        Plugin.inventoryGui.unregisterMenu(gui);
    }
}
