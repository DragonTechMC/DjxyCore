package io.github.djxy.core.commands.nodes;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;

import java.util.List;

/**
 * Created by Samuel on 2016-04-09.
 */
public class MultipleNode extends Node {

    private final String name;

    public MultipleNode(String alias, String name) {
        super(alias);
        this.name = name;
    }

    public MultipleNode(String alias) {
        this(alias, alias);
    }

    @Override
    public Node addNode(Node node) {
        return this;
    }

    @Override
    public Node getNode(String node) {
        return this;
    }

    @Override
    protected List<String> complete(String complete) {
        return EMPTY;
    }

    @Override
    public List<String> getSuggestion(String[] args, int index){
        return EMPTY;
    }

    @Override
    public void createCommandCalled(CommandCalled commandCalled, CommandSource source, String[] args, int index) throws CommandException {
        String text = "";

        for(int i = index; i < args.length; i++)
            text += args[i]+" ";

        commandCalled.addValue(name, text.trim());

        commandCalled.setExecutor(getExecutor());
    }
}
