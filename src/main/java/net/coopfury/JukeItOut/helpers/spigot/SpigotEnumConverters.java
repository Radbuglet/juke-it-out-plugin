package net.coopfury.JukeItOut.helpers.spigot;

import net.coopfury.JukeItOut.helpers.java.EnumConverter;
import net.coopfury.JukeItOut.helpers.java.EnumStringConverter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public final class SpigotEnumConverters {
    public static final EnumConverter<String, DyeColor> DYE_COLOR = new EnumStringConverter<DyeColor>()
            .addMapping("WHITE", DyeColor.WHITE)
            .addMapping("ORANGE", DyeColor.ORANGE)
            .addMapping("MAGENTA", DyeColor.MAGENTA)
            .addMapping("LIGHT_BLUE", DyeColor.LIGHT_BLUE)
            .addMapping("YELLOW", DyeColor.YELLOW)
            .addMapping("LIME", DyeColor.LIME)
            .addMapping("PINK", DyeColor.PINK)
            .addMapping("GRAY", DyeColor.GRAY)
            .addMapping("SILVER", DyeColor.SILVER)
            .addMapping("CYAN", DyeColor.CYAN)
            .addMapping("PURPLE", DyeColor.PURPLE)
            .addMapping("BLUE", DyeColor.BLUE)
            .addMapping("BROWN", DyeColor.BROWN)
            .addMapping("GREEN", DyeColor.GREEN)
            .addMapping("RED", DyeColor.RED)
            .addMapping("BLACK", DyeColor.BLACK);

    public static final EnumConverter<DyeColor, ChatColor> DYE_TO_CHAT = new EnumConverter<DyeColor, ChatColor>()
            .addMapping(DyeColor.WHITE, ChatColor.WHITE)
            .addMapping(DyeColor.ORANGE, ChatColor.GOLD)
            .addMapping(DyeColor.MAGENTA, ChatColor.DARK_PURPLE)
            .addMapping(DyeColor.LIGHT_BLUE, ChatColor.BLUE)
            .addMapping(DyeColor.YELLOW, ChatColor.YELLOW)
            .addMapping(DyeColor.LIME, ChatColor.GREEN)
            .addMapping(DyeColor.PINK, ChatColor.LIGHT_PURPLE)
            .addMapping(DyeColor.GRAY, ChatColor.DARK_GRAY)
            .addMapping(DyeColor.SILVER, ChatColor.GRAY)
            .addMapping(DyeColor.BLUE, ChatColor.BLUE)
            .addMapping(DyeColor.GREEN, ChatColor.GREEN)
            .addMapping(DyeColor.RED, ChatColor.RED)
            .addMapping(DyeColor.BLACK, ChatColor.BLACK);
}
