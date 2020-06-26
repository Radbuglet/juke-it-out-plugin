package net.coopfury.JukeItOut.helpers.virtualCommand;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.config.ConfigDictionary;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Set;

public final class VirtCommandUtils {
    // Map editing
    private static final String message_duplicate_entry_name = ChatColor.RED + "Duplicate entry name!";
    private static final String message_unknown_entry_name = ChatColor.RED + "Unknown entry name!";

    public static void registerMapEditingSubs(CommandRouter<? extends CommandSender> router, ConfigDictionary<?> map) {
        router.registerSub("list", new FixedArgCommand<>(new String[]{}, (parent, sender, args) -> {
            Set<String> keys = map.keySet();
            if (keys.size() == 0) {
                sender.sendMessage(ChatColor.RED + "No entries have been created!");
                return false;
            }
            sender.sendMessage(String.format(ChatColor.GREEN + "Entries (%s):", keys.size()));
            for (String key : keys) {
                sender.sendMessage(Constants.message_list_prefix + key);
            }
            return true;
        }));

        router.registerSub("rename", new FixedArgCommand<>(new String[]{ "old_name", "new_name" },
                (parent, sender, args) -> transferMapEntryShared(map, sender, args, true)));

        router.registerSub("clone", new FixedArgCommand<>(new String[]{ "original_name", "clone_name" },
                (parent, sender, args) -> transferMapEntryShared(map, sender, args, false)));

        router.registerSub("remove", new FixedArgCommand<>(new String[]{ "name" }, (parent, sender, args) -> {
            if (!map.contains(args.getPart(0))) {
                sender.sendMessage(message_unknown_entry_name);
                return false;
            }
            map.remove(args.getPart(0));
            sender.sendMessage(ChatColor.GREEN + "Removed entry!");
            return true;
        }));
    }

    private static boolean transferMapEntryShared(ConfigDictionary<?> map, CommandSender sender, ArgumentList args, boolean isRename) {
        if (!map.contains(args.getPart(0))) {
            sender.sendMessage(message_unknown_entry_name);
            return false;
        }
        if (map.contains(args.getPart(1))) {
            sender.sendMessage(message_duplicate_entry_name);
            return false;
        }

        Object rawValue = map.getRaw(args.getPart(0));
        if (isRename) map.remove(args.getPart(0));
        map.put(args.getPart(1), rawValue);
        sender.sendMessage(isRename ?
                ChatColor.GREEN + "Entry renamed successfully!" :
                ChatColor.GREEN + "Entry cloned successfully!");
        return true;
    }

    public interface MapAdditionHandler<TSender extends CommandSender> {
        Object createNew(TSender sender, String name, ArgumentList args);
    }

    public static<TSender extends CommandSender> void registerMapAdder(
            CommandRouter<TSender> router, ConfigDictionary<?> map,
            String[] constructorArgs, MapAdditionHandler<TSender> innerHandler) {

        FixedArgCommand<Object, TSender> handler = new FixedArgCommand<>(new String[]{"name"}, (parent, sender, args) -> {
            String desiredName = args.getPart(0);
            if (map.contains(desiredName)) {
                sender.sendMessage(message_duplicate_entry_name);
                return false;
            }
            args.rootOffset++;
            Object newValue = innerHandler.createNew(sender, desiredName, args);
            args.rootOffset--;
            if (newValue != null) {
                map.put(desiredName, newValue);
                sender.sendMessage(ChatColor.GREEN + "Entry created successfully!");
                return true;
            } else {
                return false;  // Error is sent by handler.
            }
        });
        handler.addArgs(constructorArgs);
        router.registerSub("add", handler);
    }

    // Misc
    public static String formatUsageStart(ArgumentList list) {
        return ChatColor.RED + "Usage: " + ChatColor.WHITE + list.getLeftStr(true);
    }
}
