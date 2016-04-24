package io.github.djxy.core.commands.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Samuel on 2016-04-09.
 */
public class ChoiceNode extends Node {

    private HashMap<String,Node> nodes = new HashMap<>();

    public ChoiceNode(String alias) {
        super(alias);
    }

    @Override
    protected List<String> complete(String complete){
        List<String> list = new ArrayList<>();

        for(String alias : nodes.keySet())
            if(alias.toLowerCase().startsWith(complete))
                list.add(alias);

        return list;
    }

    @Override
    public Node getNode(String node){
        return nodes.get(node);
    }

    @Override
    public Node addNode(Node node){
        nodes.put(node.alias, node);

        return this;
    }

}
