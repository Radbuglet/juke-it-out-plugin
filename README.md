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

### Event Trees

In the future, there might be a third interesting system called the "event tree". However, I wanted to finish the implementation before dealing with the technical debt as the debt doesn't really affect what I'm trying to do. Is it foolish to put off the resolution of technical debt? Probably.

The event tree attempts to solve a big limitation in my current design: game states are completely distinct. While I can easily share logic between multiple game states by decoupling the shared logic from the game state object, sharing event handlers is not that easy. The easiest way to do this would be register the handler in the game state and remember to unregister it once the game state is swapped. However, doing so is quite annoying and tricky and this turns out to be a fairly common pattern and as such, I would like to implement an abstraction that takes care of nested event handler hierarchies for me.

In order to make this change, I would first decouple event handlers and game logic. This has a few notable benefits:

- It would allow me to enforce a single handler instance rule at a global level. It doesn't make sense to have more than one handler for a specific task however it could make sense to have multiple game state controllers in the case where multiple games are hosted on one server.
- It would facilitate headless unit testing.
- It could help separate game logic and Bukkit handling logic, making things easier to maintain.

The actual abstraction providing this behavior would consist of multiple classes:

- Core tree definition classes:
  - `EventHandlerType`: an object implementing `org.bukkit.event.Listener`. Events would be declared here like in any other Bukkit event listener. It can also hook into registration and un-registration events to cleanup any remaining resources (e.g. `BukkitRunnables` and other threads)
  - `EventTreeNode`: the node forming the event tree. Contains references to its children and can optionally be associated with an `EventHandlerType` (a null `EventHandlerType` makes the node a dummy node). The node can also provide tasks to perform when it enters and leaves the tree. These are used to perform and undo the configuration changes made to the `EventHandlerType` singletons (e.g. register and unregister a reference to a relevant controller object).
  - `EventTreeRoot`: the class at the root of the event tree. Provides methods to interact with the tree and fetch event handler singletons by their type. The tree root will also register the event handler if it hasn't been registered yet and unregister it once no more event handlers exist.
- Handle utility classes:
  - `RegisteredEventList`: a list of events that have been registered in the tree that can be cleared using the destroy method. This allows the game logic objects to easily manage which events are in the tree without having to worry about cleanup.
  - `RegisterEventSingular`: same as a `RegisteredEventList` except it only stores one value at a time. This is most useful in the game state handler as there is only ever one event handler active at a time (shared events are composed using the new tree system).