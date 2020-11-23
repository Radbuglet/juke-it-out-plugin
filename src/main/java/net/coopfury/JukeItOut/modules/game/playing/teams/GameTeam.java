package net.coopfury.JukeItOut.modules.game.playing.teams;

import net.coopfury.JukeItOut.helpers.game.AbstractTeam;
import net.coopfury.JukeItOut.helpers.java.CastUtils;
import net.coopfury.JukeItOut.helpers.spigot.SpigotEnumConverters;
import net.coopfury.JukeItOut.modules.config.ConfigTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;

import java.util.Optional;

public class GameTeam extends AbstractTeam<GameMember> {
    public final ConfigTeam configTeam;

    public GameTeam(ConfigTeam configTeam) {
        this.configTeam = configTeam;
    }

    public Optional<ChatColor> getTextColor() {
        return configTeam.getWoolColor().flatMap(SpigotEnumConverters.DYE_TO_CHAT::parse);
    }

    public Optional<Chest> getTeamChest() {
        return configTeam.getChestLocation()  // Get location
                .map(Location::getBlock)  // Get block
                .flatMap(block -> CastUtils.dynamicCast(Chest.class, block.getState()));  // Get state
    }
}
