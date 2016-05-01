package io.github.djxy.core.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.djxy.core.CorePlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-04-27.
 */
public class Github {

    public Release getLatestRelease(CorePlugin corePlugin){
        String request = request(corePlugin.getGithubApiURL() + "/releases/latest");

        if(request != null) {
            JsonObject release = new JsonParser().parse(request).getAsJsonObject();
            String version = release.get("name").getAsString();

            if(version.equals(corePlugin.getVersion()))
                return null;
            else
                return new Release(corePlugin.getName(), version, release.get("html_url").getAsString());
        }
        else
            return null;
    }

    public List<FileUpdate> getTranslationUpdates(CorePlugin corePlugin){
        String request = request(corePlugin.getGithubApiURL() + "/contents/translations");

        if(request != null) {
            JsonArray updates = new JsonParser().parse(request).getAsJsonArray();
            List<FileUpdate> fileUpdates = new ArrayList<>();

            for(int i = 0; i < updates.size(); i++){
                JsonObject update = updates.get(i).getAsJsonObject();

                fileUpdates.add(new FileUpdate(
                        corePlugin.getName(),
                        update.get("name").getAsString(),
                        update.get("download_url").getAsString(),
                        update.get("sha").getAsString(),
                        update.get("size").getAsInt()
                ));
            }

            return fileUpdates;
        }
        else
            return null;
    }

    public String downloadFileContent(String downloadURL, int size){
        try {
            URL url = new URL(downloadURL);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer buffer = new StringBuffer(size);

            while ((line = br.readLine()) != null)
                buffer.append(line+"\n");
            br.close();

            return buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private String request(String httpUrl){
        try {
            URL url = new URL(httpUrl);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            String response = "";

            while ((line = br.readLine()) != null)
                response += line+"\n";
            br.close();

            return response;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public class FileUpdate {

        private final String plugin;
        private final String name;
        private final String downloadUrl;
        private final String sha;
        private final int size;

        public FileUpdate(String plugin, String name, String downloadUrl, String sha, int size) {
            this.plugin = plugin;
            this.name = name;
            this.downloadUrl = downloadUrl;
            this.sha = sha;
            this.size = size;
        }

        public String getPlugin() {
            return plugin;
        }

        public String getName() {
            return name;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public String getSha() {
            return sha;
        }

        public int getSize() {
            return size;
        }
    }

    public class Release {

        private final String name;
        private final String version;
        private final String url;

        public Release(String name, String version, String url) {
            this.name = name;
            this.version = version;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

    }

}
