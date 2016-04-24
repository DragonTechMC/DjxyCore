package io.github.djxy.core.commands.nodes;

import io.github.djxy.core.commands.CommandExecutor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;

/**
 * Created by Samuel on 2016-04-09.
 */
public abstract class ArgumentNode extends Node {

    private Node next = new ChoiceNode("");
    private final String name;

    abstract public Object getValue(String arg);

    public ArgumentNode(String alias, String name) {
        super(alias);
        this.name = name;
    }

    public ArgumentNode(String alias) {
        this(alias, alias);
    }

    public String getName() {
        return name;
    }

    @Override
    public Node getNode(String node){
        return next;
    }

    @Override
    public Node addNode(Node node){
        next.addNode(node);
        return this;
    }

    @Override
    public void createCommandCalled(CommandCalled commandCalled, String[] args, int index) throws CommandException {
        if(index+1 < args.length){
            Node next = getNode(args[index]);
            Object value = getValue(args[index]);

            if(value != null)
                commandCalled.addValue(name, value);
            else
                throw new CommandException(Text.of(CommandExecutor.WARNING_COLOR, args[index], CommandExecutor.RESET_COLOR, " is not a valid value."));

            if(next != null)
                next.createCommandCalled(commandCalled, args, index + 1);
        }
        else{
            if(args.length > index)
                commandCalled.addValue(name, getValue(args[index]));
            else
                throw new CommandException(Text.of(CommandExecutor.RESET_COLOR, "You have to set a value for ", CommandExecutor.WARNING_COLOR, alias, CommandExecutor.RESET_COLOR, "."));

            commandCalled.setExecutor(getExecutor());
        }
    }

}
