package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.commands.nodes.ArgumentNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Samuel on 2016-04-03.
 */
public class BooleanNode extends ArgumentNode {

    private static final List<String> nodesTrue = Arrays.asList("true", "yes", "y", "1");
    private static final List<String> nodesFalse = Arrays.asList("false", "no", "n", "0");
    private static final List<String> nodes;

    static {
        nodes = new ArrayList<>();
        nodes.addAll(nodesTrue);
        nodes.addAll(nodesFalse);
    }

    public BooleanNode(String alias) {
        super(alias);
    }

    public BooleanNode(String alias, String name) {
        super(alias, name);
    }

    @Override
    public Object getValue(String argument) {
        if(nodesTrue.contains(argument))
            return true;
        if(nodesFalse.contains(argument))
            return false;
        else
            return null;
    }

    @Override
    public List<String> complete(String complete) {
        List<String> values = new ArrayList<>();

        for (String value : nodes)
            if (value.startsWith(complete))
                values.add(value);

        return values;
    }
}
