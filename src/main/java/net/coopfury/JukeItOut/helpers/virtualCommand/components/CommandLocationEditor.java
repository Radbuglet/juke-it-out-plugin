package net.coopfury.JukeItOut.helpers.virtualCommand.components;

import net.coopfury.JukeItOut.helpers.virtualCommand.ArgumentList;
import net.coopfury.JukeItOut.helpers.virtualCommand.CommandContext;
import net.coopfury.JukeItOut.helpers.virtualCommand.VirtualCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CommandLocationEditor implements VirtualCommandHandler<Player> {
    public final Consumer<Location> setLocation;
    public final Supplier<Optional<Location>> getLocation;
    private final SubCommandRouter<Player> router = new SubCommandRouter<>();

    public CommandLocationEditor(Consumer<Location> setLocation, Supplier<Optional<Location>> getLocation) {
        this.setLocation = setLocation;
        this.getLocation = getLocation;

        router.registerSub("set", (sender, args, context) -> {
            setLocation.accept(sender.getLocation());
            sender.sendMessage(ChatColor.GREEN + "Set location successfully.");
            return true;
        });

        router.registerSub("tp", (sender, args, context) -> {
            Optional<Location> location = getLocation.get();
            if (location.isPresent()) {
                sender.teleport(location.get());
                sender.sendMessage(ChatColor.GREEN + "Whoosh!");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to teleport: no location set!");
                return false;
            }
        });
    }

    @Override
    public boolean runCommand(Player sender, ArgumentList args, CommandContext context) {
        return router.runCommand(sender, args, context);
    }
}
