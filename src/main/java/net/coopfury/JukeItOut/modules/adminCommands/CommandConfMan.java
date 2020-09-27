package net.coopfury.JukeItOut.modules.adminCommands;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.config.ConfigDictionary;
import net.coopfury.JukeItOut.helpers.config.ConfigEditingUtils;
import net.coopfury.JukeItOut.helpers.spigot.SpigotEnumConverters;
import net.coopfury.JukeItOut.helpers.virtualCommand.*;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Consumer;

class CommandConfMan extends PlayerCommandVirtualForward {
    private static final String message_invalid_team_color = ChatColor.RED + "Invalid color. Color must be a valid DyeColor.";
    private static ConfigLoadingModule getConfig() {
        return Plugin.getModule(ConfigLoadingModule.class);
    }

    private static final CommandRouter<Player> router = new CommandRouter<Player>()
            .registerSub("teams", new CommandRouter<Player>()
                    .registerMultiple(router -> {
                        ConfigDictionary<ConfigTeam> map = getConfig().root.getTeams();

                        // Generic editing subs
                        ConfigEditingUtils.registerMapEditingSubs(router, map);
                        ConfigEditingUtils.registerMapAdder(router, map, new String[]{"color"}, (sender, name, args, instance) -> {
                            if (!SpigotEnumConverters.DYE_COLOR.isValid(args.getPart(0))) {
                                sender.sendMessage(message_invalid_team_color);
                                return false;
                            }

                            instance.setName(name);
                            instance.setWoolColor(args.getPart(0));
                            instance.setSpawnLocation(sender.getLocation());
                            return true;
                        });

                        router.registerSub("recolor", ConfigEditingUtils.makeMapEditingHandler(map, new String[]{"color"}, (sender, args, entry) -> {
                            String newColor = args.getPart(0);
                            if (!SpigotEnumConverters.DYE_COLOR.isValid(newColor)) {
                                sender.sendMessage(message_invalid_team_color);
                                return false;
                            }
                            entry.setWoolColor(newColor);
                            sender.sendMessage(ChatColor.GREEN + "Successfully recolored team!");
                            return true;
                        }));

                        router.registerSub("set-location", ConfigEditingUtils.makeMapEditingHandler(
                                map, new String[]{"jukebox|chest"}, ((sender, args, team) -> {  // TODO: Support for enums, make this automatic.
                                    Consumer<Location> setter;
                                    String locationName = args.getPart(0);
                                    if (locationName.equals("jukebox")) {
                                        setter = team::setJukeboxLocation;
                                    } else if (locationName.equals("chest")) {
                                        setter = team::setChestLocation;
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "Location name should either be jukebox or chest. Got " + locationName + "!");
                                        return false;
                                    }

                                    Optional<Location> location = VirtCommandUtils.getTargetBlockReasoned(sender, 5);
                                    if (!location.isPresent())  // Error message handled by method above.
                                        return false;

                                    setter.accept(location.get());
                                    sender.sendMessage(ChatColor.GREEN + "Location set to block you were looking at.");
                                    return true;
                        })));
                    })
            )
            .registerSub("set-diamond-spawn", new FixedArgCommand<>(new String[]{}, (sender, args) -> {
                getConfig().root.setDiamondSpawn(sender.getLocation());
                sender.sendMessage(ChatColor.GREEN + "Set diamond spawn location!");
                return true;
            }))
            .registerSub("set-lobby-spawn", new FixedArgCommand<>(new String[]{}, (sender, args) -> {  // TODO: This is awful. We *need* to restructure virtual commands!!!
                getConfig().root.setLobbySpawn(sender.getLocation());
                sender.sendMessage(ChatColor.GREEN + "Set lobby spawn location!");
                return true;
            }))
            .registerSub("save", new FixedArgCommand<>(new String[]{}, (sender, args) -> {
                getConfig().saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Saved config!");
                return true;
            }))
            .registerSub("reload", new FixedArgCommand<>(new String[]{}, (sender, args) -> {
                getConfig().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Reloaded config!");
                return true;
            }));

    @Override
    protected VirtualCommandHandler<Player> getHandler() {
        return router;
    }
}
