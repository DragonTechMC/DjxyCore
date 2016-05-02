package io.github.djxy.core.network.github;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.network.NetworkUtil;
import io.github.djxy.core.repositories.FileUpdateRepository;
import io.github.djxy.core.repositories.PluginUpdateRepository;

/**
 * Created by Samuel on 2016-04-27.
 */
public class Github {

    public void checkLatestRelease(CorePlugin corePlugin){
        String request = NetworkUtil.requestHttps(corePlugin.getGithubApiURL() + "/releases/latest");

        if(request != null) {
            JsonObject release = new JsonParser().parse(request).getAsJsonObject();
            String version = release.get("tag_name").getAsString();

            if(!version.equals(corePlugin.getVersion()))
                new PluginUpdateRepository.PluginUpdate(corePlugin.getName(), version, release.get("html_url").getAsString());
        }
    }

    public void checkTranslationUpdates(CorePlugin corePlugin){
        String request = NetworkUtil.requestHttps(corePlugin.getGithubApiURL() + "/contents/translations");

        if(request != null) {
            JsonArray updates = new JsonParser().parse(request).getAsJsonArray();

            for(int i = 0; i < updates.size(); i++){
                JsonObject update = updates.get(i).getAsJsonObject();

                new FileUpdateRepository.FileUpdate(
                        corePlugin,
                        update.get("name").getAsString(),
                        update.get("download_url").getAsString(),
                        update.get("sha").getAsString(),
                        update.get("size").getAsInt()
                );
            }

        }
    }

}
