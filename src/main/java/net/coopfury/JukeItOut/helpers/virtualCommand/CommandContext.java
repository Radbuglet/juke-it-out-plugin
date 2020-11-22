package net.coopfury.JukeItOut.helpers.virtualCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandContext<TSender extends CommandSender> {
    private final Map<String, Object> parsed = new HashMap<>();
    private int argLoc = 0;

    public final TSender sender;
    private final String name;
    private final String[] args;

    public CommandContext(TSender sender, String name, String[] args) {
        this.sender = sender;
        this.name = name;
        this.args = args;
    }

    public Optional<String> consumeArg() {
        return argLoc >= args.length ? Optional.empty() : Optional.of(args[argLoc++]);
    }

    public Optional<String> peekArg() {
        return argLoc >= args.length ? Optional.empty() : Optional.of(args[argLoc]);
    }

    public void putParsedArg(String id, Object value) {
        if (parsed.containsKey(id)) {
            Bukkit.getLogger().warning("Parsed arg at id " + id + " got its value overwritten!");
        }
        parsed.put(id, value);
    }

    public<T> Optional<T> getParsedArg(String id) {
        Object value = parsed.get(id);
        if (value == null) return Optional.empty();

        //noinspection unchecked
        return Optional.of((T) value);
    }

    public void sendArgError(String text) {
        sendArgError(text, null);
    }

    public void sendArgError(String text, String locLineSuffix) {
        sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + text);

        StringBuilder locationBuilder = new StringBuilder(ChatColor.RED + "   in " + ChatColor.GRAY + "/");
        locationBuilder.append(name);

        // TODO: In the future, this should highlight all recently processed parts of the commands
        for (int index = 0; index < args.length; index++) {
            locationBuilder.append(" ");
            if (index == argLoc) {
                locationBuilder.append(ChatColor.RED.toString()).append(ChatColor.UNDERLINE);
            }
            locationBuilder.append(args[0]);
            if (index == argLoc) {
                locationBuilder.append(ChatColor.RED.toString());
            }
        }

        if (locLineSuffix != null) {
            locationBuilder.append(ChatColor.RED.toString()).append(ChatColor.UNDERLINE).append(locLineSuffix);
        }

        sender.sendMessage(locationBuilder.toString());
    }

    public static<TSender extends CommandSender> boolean run(TSender user, String commandName, String[] args, CommandPart<TSender> startingPart) {
        return new CommandContext<>(user, commandName, args).run(startingPart);
    }

    public boolean run(CommandPart<TSender> part) {
        while (true) {
            CommandResult<TSender> result = part.handle(this);
            if (result.isFinished) {
                return (boolean) result.value;
            }

            //noinspection unchecked
            part = (CommandPart<TSender>) result.value;
        }
    }
}
