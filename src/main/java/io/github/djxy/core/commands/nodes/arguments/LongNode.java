package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.commands.nodes.ArgumentNode;

import java.util.List;

/**
 * Created by Samuel on 2016-04-10.
 */
public class LongNode extends ArgumentNode {

    public LongNode(String alias, String name) {
        super(alias, name);
    }

    public LongNode(String alias) {
        super(alias);
    }

    @Override
    public Object getValue(String arg) {
        try{
            return Long.parseLong(arg);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    protected List<String> complete(String complete) {
        return EMPTY_LIST;
    }

}
