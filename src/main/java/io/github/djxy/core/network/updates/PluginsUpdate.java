package io.github.djxy.core.network.updates;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.network.ore.Ore;

/**
 * Created by Samuel on 2016-04-29.
 */
public class PluginsUpdate extends Update {

    private final Ore ore = new Ore();

    public PluginsUpdate(boolean async) {
        super(async);
    }

    @Override
    public void run() {
        CoreMain.getInstance().getCorePlugins().forEach(github::checkLatestRelease);
        CoreMain.getInstance().getCorePlugins().forEach(ore::checkLatestRelease);
    }

}
