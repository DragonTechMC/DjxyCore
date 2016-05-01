package io.github.djxy.core.network.updates;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.network.Github;
import io.github.djxy.core.repositories.PlayerRepository;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.action.TextActions;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Samuel on 2016-04-29.
 */
public class PluginsUpdate extends Update {

    public PluginsUpdate(List<CorePlugin> plugins) {
        super(plugins, PlayerRepository.RECEIVE_NOTIFICATION_PLUGINS);
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
                values.put("clickHereStop", TextActions.executeCallback(e -> {
                    PlayerRepository.getInstance().setPlayerData(((Player) e).getUniqueId(), PlayerRepository.RECEIVE_NOTIFICATION_TRANSLATIONS, false);
                    e.sendMessage(CoreMain.getTranslatorInstance().translate(e, "setReceiveNotificationPluginsFalse", null));
                }));

                for(Player player : players) {
                    createSyncTask(e -> {
                        player.sendMessage(CoreMain.getTranslatorInstance().translate(player, "notifyNewRelease", values));
                    });
                }
            }
        }
    }

}
