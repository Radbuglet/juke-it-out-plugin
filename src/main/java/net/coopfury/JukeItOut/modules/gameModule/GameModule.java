package net.coopfury.JukeItOut.modules.gameModule;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.modules.configLoading.ConfigLoadingModule;
import net.coopfury.JukeItOut.modules.gameModule.playing.GameStatePlaying;
import org.bukkit.event.HandlerList;

public class GameModule extends PluginModule {
    public GameState currentState;

    public void setGameState(GameState newState) {
        if (currentState != null) {
            HandlerList.unregisterAll(currentState);
        }
        Plugin.getGame().bindListener(newState);
    }

    @Override
    protected void onEnable(Plugin pluginInstance) {
        ConfigLoadingModule configLoadingModule = Plugin.getModule(ConfigLoadingModule.class);
        if (configLoadingModule.teams.size() == 0) {
            Plugin.getGame().getLogger().warning("No teams available for debug thingy!");
            return;
        }

        GameStatePlaying state = new GameStatePlaying();
        state.makeTeam(configLoadingModule.teams.get(0));
        setGameState(state);
    }
}
