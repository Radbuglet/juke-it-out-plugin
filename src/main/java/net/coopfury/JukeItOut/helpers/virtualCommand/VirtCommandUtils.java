package net.coopfury.JukeItOut.helpers.virtualCommand;

import net.coopfury.JukeItOut.helpers.config.ConfigDictionary;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public final class VirtCommandUtils {
    public static String formatUsageStart(ArgumentList list) {
        return ChatColor.RED + "Usage: " + ChatColor.WHITE + list.getLeftStr(true);
    }

    public static<TSender extends CommandSender> void registerMapEditingSubs(CommandRouter<TSender> router, ConfigDictionary<?> map) {
        throw new NotImplementedException();
    }
}
