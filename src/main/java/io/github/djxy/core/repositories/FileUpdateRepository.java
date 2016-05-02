package io.github.djxy.core.repositories;

import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.network.NetworkUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private final ConcurrentHashMap<String, List<FileUpdate>> fileUpdates = new ConcurrentHashMap<>();//Plugin/File

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

    public static class FileUpdate{

        private final CorePlugin plugin;
        private final String name;
        private final String downloadUrl;
        private final String sha;
        private final int size;

        public FileUpdate(CorePlugin plugin, String name, String downloadUrl, String sha, int size) {
            this.plugin = plugin;
            this.name = name;
            this.downloadUrl = downloadUrl;
            this.sha = sha;
            this.size = size;

            if(!instance.fileUpdates.containsKey(plugin.getName()))
                instance.fileUpdates.put(plugin.getName(), new CopyOnWriteArrayList<>());

            instance.fileUpdates.get(plugin.getName()).add(this);
        }

        public boolean canDownload() {
            return !instance.filesDownloaded.containsKey(plugin.getName()) || !instance.filesDownloaded.get(plugin.getName()).containsKey(this);
        }

        public void download(){
            if(canDownload()) {
                if (!instance.filesDownloaded.containsKey(plugin.getName()))
                    instance.filesDownloaded.put(plugin.getName(), new ConcurrentHashMap<>());

                instance.fileUpdates.get(plugin.getName()).remove(this);
                instance.filesDownloaded.get(plugin.getName()).put(this, true);
                createFile(plugin.getTranslationPath().toFile(), NetworkUtil.requestHttps(downloadUrl, size));
            }
        }

        public CorePlugin getPlugin() {
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
            return plugin.getName().hashCode()+sha.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof FileUpdate)
                return ((FileUpdate) obj).plugin.getName().equals(plugin.getName()) && ((FileUpdate) obj).sha.equals(sha);
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
