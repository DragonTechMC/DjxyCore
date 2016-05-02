package io.github.djxy.core.network.ore;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.network.NetworkUtil;
import io.github.djxy.core.repositories.PluginUpdateRepository;

/**
 * Created by Samuel on 2016-05-01.
 */
public class Ore {

    public void checkLatestRelease(CorePlugin corePlugin){
        String request = NetworkUtil.requestHttps("https://ore-staging.spongepowered.org/api/projects/"+corePlugin.getId());

        if(request != null) {
            JsonObject jsonObject = new JsonParser().parse(request).getAsJsonObject();
            JsonObject release = jsonObject.get("recommended").getAsJsonObject();

            if(!release.get("version").getAsString().equals(corePlugin.getVersion()))
                new PluginUpdateRepository.PluginUpdate(corePlugin.getName(), release.get("version").getAsString(), "https://ore-staging.spongepowered.org"+jsonObject.get("href").getAsString()+"/versions");
        }
    }

}
