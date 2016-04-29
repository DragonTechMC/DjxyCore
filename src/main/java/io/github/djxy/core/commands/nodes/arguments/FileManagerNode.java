package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.commands.nodes.ArgumentNode;
import io.github.djxy.core.files.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-04-14.
 */
public class FileManagerNode extends ArgumentNode {

    private final CorePlugin corePlugin;
    private final Class<? extends FileManager> types[];

    public FileManagerNode(String alias, String name, CorePlugin plugin, Class<? extends FileManager>... types) {
        super(alias, name);
        this.corePlugin = plugin;
        this.types = types;
    }

    public FileManagerNode(String alias, CorePlugin plugin, Class<? extends FileManager>... types) {
        super(alias, alias);
        this.corePlugin = plugin;
        this.types = types;
    }

    @Override
    public Object getValue(String arg) {
        return corePlugin.getFileManager(arg, types);
    }

    @Override
    protected List<String> complete(String complete) {
        List<String> values = new ArrayList<>();

        for(FileManager fileManager : corePlugin.getFileManagers(types))
            if(fileManager.getName().toLowerCase().startsWith(complete))
                values.add(fileManager.getName());

        return values;
    }

}
