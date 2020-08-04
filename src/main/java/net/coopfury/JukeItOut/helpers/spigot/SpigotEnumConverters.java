package net.coopfury.JukeItOut.helpers.spigot;

import net.coopfury.JukeItOut.helpers.java.EnumStringParser;
import org.bukkit.DyeColor;

public final class SpigotEnumConverters {
    public static final EnumStringParser<DyeColor> DYE_COLOR = new EnumStringParser<DyeColor>()
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
}
