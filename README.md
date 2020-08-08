# Juke It Out

Juke It Out is a minigame for Minecraft: Java Edition where players fight for control of the shared generator at the middle of the map to end up with the most diamonds at the end. There are thirty rounds in every game, each lasting thirty seconds. Every round, the players are warped back to their base and given fifteen seconds to reach and control the middle of the map until the diamond spawns. Once the diamond spawns, everyone but the person who obtained the diamond gets a counter-offensive buff and the person in control of the diamond must return back to their base to store the diamond before the round ends. Diamonds can either be stored in a chest for safe keeping or put to use in a jukebox. The jukebox can be used to give buffs to the player's team or weaken enemy players when the approach the base. In normal circumstances, the jukebox and chest are only accessible by the team who owns them, however, every five rounds, there is a defense round where these chests now become accessible, allowing teams to steal from each other. At the end of the game, the number of diamonds stored at the players' bases is counted up. The team with the most diamonds wins.

This plugin is not intended for use by other servers as it makes a few assumptions about the server's minigame system and only provides customization options for map makers.

## A few interesting implementation details

- The config is loaded using wrapper objects which operate directly on `ConfigurationSections`. This provides a few key advantages over taking the generic representation of the config and loading it into one's own data structures, notably:
  - Loading is non-destructive
  - Metadata gets preserved
  -  There is less redundancy in memory as only one memory representation is used instead of two
  - This system is easier to write
- Commands are handled using a virtual command abstraction, where normal Bukkit command handlers forward the invocation to `VirtualCommandHandlers`. Virtual command handlers take a list of arguments that gets shortened to the argument list local to that specific handler (e.g. if a handler operates on a sub-command, it will receive the list of arguments for that sub command only) as well as a sender. From there, a few generic virtual command handlers were constructed to handle sub-command routing, argument parsing, and editing subs for map-like objects (think `scoreboard objectives add/remove/rename <map key> <extra arguments>`).

The following is a theory crafted refactor for empowering the current event handling system:

- A Godot-like scene tree system is constructed out of nodes. Each node contains a list of children. When one node is freed, all its children are also freed. These nodes are used for two things: broadcasting events (in Godot, these are called notifications) and automating cleanup (e.g. cleanup on plugin disable, event un-registration on game state change).
- The plugin singleton can be obtained by searching for the scene root (this will probably still be a static field for performance reasons).
- Nodes can register multiple lifecycle/cleanup handlers called plugins.
- If a node needs to listen to Bukkit events, it can register a generic Bukkit event listener node plugin.
- There will also be a special type of node which tells the server to enable a specific event listener singleton. This would allow event handler list rules to be defined declaratively.

By doing the above, sharing behavior between game states would be much easier and much more flexible.