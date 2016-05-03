package io.github.djxy.core.repositories;

import io.github.djxy.core.network.NetworkUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Samuel on 2016-04-30.
 */
public class FileUpdateRepository {

    private static List<FileUpdate> EMPTY = new ArrayList<>();

    private static final FileUpdateRepository instance = new FileUpdateRepository();

    public static FileUpdateRepository getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<String, ConcurrentHashMap<FileUpdate, Object>> filesDownloaded = new ConcurrentHashMap<>();//Plugin/File/null
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<FileUpdate>> fileUpdates = new ConcurrentHashMap<>();//Plugin/File

    public Collection<String> getPlugins(){
        HashSet<String> plugins = new HashSet<>();

        plugins.addAll(filesDownloaded.keySet());
        plugins.addAll(fileUpdates.keySet());

        return plugins;
    }

    public List<FileUpdate> getFilesDownloaded(String plugin){
        if(filesDownloaded.containsKey(plugin))
            return new ArrayList<>(filesDownloaded.get(plugin).keySet());
        else
            return EMPTY;
    }

    public List<FileUpdate> getFileUpdates(String plugin){
        if(fileUpdates.containsKey(plugin))
            return new ArrayList<>(fileUpdates.get(plugin));
        else
            return EMPTY;
    }

    public void addDownloadedFile(String pluginName, String sha){
        if (!instance.filesDownloaded.containsKey(pluginName))
            instance.filesDownloaded.put(pluginName, new ConcurrentHashMap<>());

        instance.filesDownloaded.get(pluginName).put(new FileUpdate(pluginName, null, "", sha, -1, null), true);
    }

    public boolean hasUpdate() {
        for (String plugin : getPlugins())
            if(getFileUpdates(plugin).size() != 0)
                return true;

            return false;
    }

    public static class FileUpdate{

        private final String plugin;
        private final String name;
        private final String downloadUrl;
        private final String sha;
        private final Path pathToDownload;
        private final int size;

        public FileUpdate(String plugin, String name, String downloadUrl, String sha, int size, Path pathToDownload) {
            this.plugin = plugin;
            this.name = name;
            this.downloadUrl = downloadUrl;
            this.sha = sha;
            this.size = size;
            this.pathToDownload = pathToDownload;

            if(pathToDownload != null && canDownload()) {
                if (!instance.fileUpdates.containsKey(plugin))
                    instance.fileUpdates.put(plugin, new CopyOnWriteArraySet<>());

                instance.fileUpdates.get(plugin).add(this);
            }
        }

        public boolean canDownload() {
            return !instance.filesDownloaded.containsKey(plugin) || !instance.filesDownloaded.get(plugin).containsKey(this);
        }

        public void download(){
            if(pathToDownload != null && canDownload()) {
                if (!instance.filesDownloaded.containsKey(plugin))
                    instance.filesDownloaded.put(plugin, new ConcurrentHashMap<>());

                instance.fileUpdates.get(plugin).remove(this);
                instance.filesDownloaded.get(plugin).put(this, true);
                createFile(pathToDownload.resolve(name).toFile(), NetworkUtil.requestHttps(downloadUrl, size));
            }
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

        @Override
        public int hashCode() {
            return plugin.hashCode()+sha.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof FileUpdate)
                return ((FileUpdate) obj).plugin.equals(plugin) && ((FileUpdate) obj).sha.equals(sha);
            else
                return super.equals(obj);
        }

        private void createFile(File file, String content){
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);

                fos.write(content.getBytes("UTF-8"));

                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
