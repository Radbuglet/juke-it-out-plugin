package net.coopfury.JukeItOut.helpers.config;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.helpers.virtualCommand.ArgumentList;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandRouter;
import net.coopfury.JukeItOut.helpers.virtualCommand.FixedArgCommand;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Optional;

// TODO: The VirtualCommands model needs context! Also, design this as their own classes.
public final class ConfigEditingUtils {
    // Map editing
    private static final String message_duplicate_entry_name = ChatColor.RED + "Duplicate entry name!";
    private static final String message_unknown_entry_name = ChatColor.RED + "Unknown entry name!";

    public interface MapAdditionHandler<TSender extends CommandSender, TVal> {
        boolean createNew(TSender sender, String name, ArgumentList args, TVal value);
    }

    public interface MapEntryEditingHandler<TSender extends CommandSender, TEntry> {
        boolean performEdits(TSender sender, ArgumentList args, TEntry entry);
    }

    public static<TSender extends CommandSender, TEntry> VirtualCommandHandler<TSender> makeMapEditingHandler(
            ConfigDictionary<TEntry> map, String[] innerArgs, MapEntryEditingHandler<TSender, TEntry> innerHandler) {
        FixedArgCommand<TSender> command = new FixedArgCommand<TSender>(new String[]{"name"}, (sender, args) -> {
            Optional<TEntry> entry = map.get(args.getPart(0));
            if (!entry.isPresent()) {
                sender.sendMessage(message_unknown_entry_name);
                return false;
            }

            args.rootOffset++;
            boolean success = innerHandler.performEdits(sender, args, entry.get());
            args.rootOffset--;
            return success;
        });
        command.addArgs(innerArgs);
        return command;
    }

    public static<TSender extends CommandSender, TVal> void registerMapAdder(
            CommandRouter<TSender> router, ConfigDictionary<TVal> map,
            String[] constructorArgs, MapAdditionHandler<TSender, TVal> innerHandler) {

        FixedArgCommand<TSender> handler = new FixedArgCommand<>(new String[]{"name"}, (sender, args) -> {
            String desiredName = args.getPart(0);
            if (map.has(desiredName)) {
                sender.sendMessage(message_duplicate_entry_name);
                return false;
            }
            args.rootOffset++;
            TVal value = map.create(desiredName);
            boolean success = innerHandler.createNew(sender, desiredName, args, value);
            args.rootOffset--;
            if (success) {
                sender.sendMessage(ChatColor.GREEN + "Entry created successfully!");
                return true;
            } else {
                map.remove(desiredName);
                return false;  // Error message is sent by handler.
            }
        });
        handler.addArgs(constructorArgs);
        router.registerSub("add", handler);
    }

    public static void registerMapEditingSubs(CommandRouter<? extends CommandSender> router, ConfigDictionary<?> map) {
        router.registerSub("list", new FixedArgCommand<>(new String[]{}, (sender, args) -> {
            Collection<String> keys = map.keys();
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
                (sender, args) -> transferMapEntryShared(map, sender, args, true)));

        router.registerSub("clone", new FixedArgCommand<>(new String[]{ "original_name", "clone_name" },
                (sender, args) -> transferMapEntryShared(map, sender, args, false)));

        router.registerSub("remove", new FixedArgCommand<>(new String[]{ "name" }, (sender, args) -> {
            if (!map.has(args.getPart(0))) {
                sender.sendMessage(message_unknown_entry_name);
                return false;
            }
            map.remove(args.getPart(0));
            sender.sendMessage(ChatColor.GREEN + "Removed entry!");
            return true;
        }));
    }

    private static boolean transferMapEntryShared(ConfigDictionary<?> map, CommandSender sender, ArgumentList args, boolean isRename) {
        if (!map.has(args.getPart(0))) {
            sender.sendMessage(message_unknown_entry_name);
            return false;
        }
        if (map.has(args.getPart(1))) {
            sender.sendMessage(message_duplicate_entry_name);
            return false;
        }

        Object rawValue = map.get(args.getPart(0));
        if (isRename) map.remove(args.getPart(0));
        map.setRaw(args.getPart(1), rawValue);
        sender.sendMessage(isRename ?
                ChatColor.GREEN + "Entry renamed successfully!" :
                ChatColor.GREEN + "Entry cloned successfully!");
        return true;
    }
}
