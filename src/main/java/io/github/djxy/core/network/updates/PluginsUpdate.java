package io.github.djxy.core.network.updates;

import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.network.Github;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.action.TextActions;

import java.util.HashMap;

/**
 * Created by Samuel on 2016-04-29.
 */
public class PluginsUpdate extends Update {

    /**
     * Never forget it could be async with the server.
     *
     * @param plugin
     */
    public PluginsUpdate(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void run() {
        for(CorePlugin corePlugin : plugins){
            Github.Release release = github.getLatestRelease(corePlugin);

            if(release != null && !release.getVersion().equals(corePlugin.getVersion())){
                HashMap<String,Object> values = new HashMap<>();
                values.put("version", release.getVersion());
                values.put("plugin", release.getName());
                values.put("clickHereUrl", TextActions.openUrl(createURL(release.getUrl())));
                values.put("clickHereStop", TextActions.executeCallback(e -> {}));

                for(Player player : players) {
                    createSyncTask(e -> {
                        player.sendMessage(plugin.getTranslator().translate(player, "notifyNewRelease", values));
                    });
                }
            }
        }
    }
}
