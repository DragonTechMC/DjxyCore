package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.commands.nodes.ArgumentNode;
import io.github.djxy.core.files.FileFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-04-09.
 */
public class FileFormatNode extends ArgumentNode {

    public FileFormatNode(String alias, String name) {
        super(alias, name);
    }

    public FileFormatNode(String alias) {
        super(alias);
    }

    @Override
    public Object getValue(String arg) {
        try{
            return FileFormat.valueOf(arg);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    protected List<String> complete(String complete) {
        List<String> values = new ArrayList<>();

        for (FileFormat value : FileFormat.values())
            if (value.name().toLowerCase().startsWith(complete))
                values.add(value.name());

        return values;
    }

}
