package io.github.djxy.core.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-04-30.
 */
public class FileUpdateRepository {

    private static List<String> EMPTY = new ArrayList<>();

    private static final FileUpdateRepository instance = new FileUpdateRepository();

    public static FileUpdateRepository getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> filesDownloaded = new ConcurrentHashMap<>();//Plugin/Sha/null

    public boolean canDownloadFile(String plugin, String sha){
        if(filesDownloaded.containsKey(plugin))
            return !filesDownloaded.get(plugin).containsKey(sha);
        else
            return true;
    }

    public void addFileDownloaded(String plugin, String sha){
        if(!filesDownloaded.containsKey(plugin))
            filesDownloaded.put(plugin, new ConcurrentHashMap<>());

        filesDownloaded.get(plugin).put(sha, true);
    }

    public List<String> getPlugins(){
        return new ArrayList<>(filesDownloaded.keySet());
    }

    public List<String> getFiles(String plugin){
        if(filesDownloaded.containsKey(plugin))
            return new ArrayList<>(filesDownloaded.get(plugin).keySet());
        else
            return EMPTY;
    }

}
