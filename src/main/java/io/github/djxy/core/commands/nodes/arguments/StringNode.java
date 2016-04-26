package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.commands.nodes.ArgumentNode;

import java.util.List;

/**
 * Created by Samuel on 2016-04-07.
 */
public class StringNode extends ArgumentNode {

    public StringNode(String alias, String name) {
        super(alias, name);
    }

    public StringNode(String alias) {
        super(alias);
    }

    @Override
    public Object getValue(String arg) {
        return arg;
    }

    @Override
    protected List<String> complete(String complete) {
        return EMPTY_LIST;
    }

}
