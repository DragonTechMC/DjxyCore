package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.commands.nodes.ArgumentNode;
import io.github.djxy.core.files.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Samuel on 2016-04-14.
 */
public class FileManagerNode extends ArgumentNode {

    private final HashMap<String,FileManager> fileManagers = new HashMap<>();

    public FileManagerNode(String alias, String name, FileManager... fileManagers) {
        super(alias, name);

        for(FileManager fileManager : fileManagers)
            this.fileManagers.put(fileManager.getName(), fileManager);
    }

    public FileManagerNode(String alias, FileManager... fileManagers) {
        super(alias);

        for(FileManager fileManager : fileManagers)
            this.fileManagers.put(fileManager.getName(), fileManager);
    }

    @Override
    public Object getValue(String arg) {
        return fileManagers.get(arg);
    }

    @Override
    protected List<String> complete(String complete) {
        List<String> values = new ArrayList<>();

        for(String value : fileManagers.keySet())
            if(value.toLowerCase().startsWith(complete))
                values.add(value);

        return values;
    }

}
