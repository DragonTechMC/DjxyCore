package io.github.djxy.core.network.updates;

import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.network.Github;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-04-29.
 */
public abstract class Update implements Runnable {

    protected final CorePlugin plugin;
    protected final Github github = new Github();
    protected List<Player> players;
    protected List<CorePlugin> plugins;

    /**
     * Never forget it could be async with the server.
     * @param plugin
     */
    public Update(CorePlugin plugin) {
        this.plugin = plugin;
    }

    public void setPlayersToNotify(List<Player> players) {
        this.players = players;
    }

    public void setPluginsToCheck(List<CorePlugin> plugins) {
        this.plugins = plugins;
    }

    protected void createSyncTask(Consumer task){
        Sponge.getScheduler().createTaskBuilder().execute(task).submit(plugin);
    }

    protected URL createURL(String url){
        try {
            return new URL(url);
        } catch (MalformedURLException e) {}

        return null;
    }

}
