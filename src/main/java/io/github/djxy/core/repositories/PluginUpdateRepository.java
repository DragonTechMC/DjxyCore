package io.github.djxy.core.repositories;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.CorePlugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Samuel on 2016-04-30.
 */
public class PluginUpdateRepository {

    private static final PluginUpdateRepository instance = new PluginUpdateRepository();

    public static PluginUpdateRepository getInstance() {
        return instance;
    }

    private final CopyOnWriteArraySet<PluginUpdate> pluginUpdates = new CopyOnWriteArraySet<>();

    public Collection<PluginUpdate> getPluginUpdates(){
        return new ArrayList<>(pluginUpdates);
    }

    public boolean hasUpdate(){
        return !pluginUpdates.isEmpty();
    }

    public static class PluginUpdate {

        private final String name;
        private final String version;
        private final String url;
        private boolean newUpdate;

        public PluginUpdate(String name, String version, String url) {
            this.name = name;
            this.version = version;
            this.url = url;
            this.newUpdate = false;

            for(CorePlugin corePlugin : CoreMain.getInstance().getCorePlugins()) {
                if (name.equals(corePlugin.getName()) && !version.equals(corePlugin.getVersion())){
                    newUpdate = true;
                    instance.pluginUpdates.add(this);
                }
            }
        }

        public boolean isNewUpdate(){
            return newUpdate;
        }

        public URL getUrl() {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                return null;
            }
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof PluginUpdate)
                return ((PluginUpdate) obj).name.equals(name);
            else
                return super.equals(obj);
        }
    }

}
