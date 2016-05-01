package io.github.djxy.core.files.fileManagers;

import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.repositories.FileUpdateRepository;
import ninja.leaping.configurate.ConfigurationNode;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by Samuel on 2016-04-30.
 */
public class FileUpdateRepositoryFile extends FileManager {

    public FileUpdateRepositoryFile(Path folder) {
        super(folder, "fileUpdates");
    }

    @Override
    protected void save(ConfigurationNode root) {
        FileUpdateRepository fur = FileUpdateRepository.getInstance();

        for(String plugin : fur.getPlugins())
            root.getNode(plugin).setValue(fur.getFiles(plugin));
    }

    @Override
    protected void load(ConfigurationNode root) {
        FileUpdateRepository fur = FileUpdateRepository.getInstance();
        Map<Object, ConfigurationNode> plugins = (Map<Object, ConfigurationNode>) root.getChildrenMap();

        for(Object plugin : plugins.keySet()){
            List<ConfigurationNode> files = (List<ConfigurationNode>) plugins.get(plugin).getChildrenList();

            for(ConfigurationNode file : files)
                fur.addFileDownloaded((String) plugin, file.getString());
        }
    }

}
