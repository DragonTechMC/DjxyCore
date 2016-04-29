package io.github.djxy.core.commands.nodes;

import io.github.djxy.core.CoreUtil;
import io.github.djxy.core.CoreMain;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
    public void createCommandCalled(CommandCalled commandCalled, CommandSource source, String[] args, int index) throws CommandException {
        if(index+1 < args.length){
            Node next = getNode(args[index]);
            Object value = getValue(args[index]);

            if(value != null)
                commandCalled.addValue(name, value);
            else
                throw new CommandException(Text.of(TextColors.WHITE).concat(CoreMain.getTranslatorInstance().translate(source, "commandValueNotValid", CoreUtil.createMap("value", args[index]), false)));

            if(next != null)
                next.createCommandCalled(commandCalled, source, args, index + 1);
        }
        else{
            if(args.length > index){
                Object value = getValue(args[index]);

                if(value != null)
                    commandCalled.addValue(name, value);
                else
                    throw new CommandException(Text.of(TextColors.WHITE).concat(CoreMain.getTranslatorInstance().translate(source, "commandValueNotValid", CoreUtil.createMap("value", args[index]), false)));
            }
            else
                throw new CommandException(Text.of(TextColors.WHITE).concat(CoreMain.getTranslatorInstance().translate(source, "commandNotComplete", CoreUtil.createMap("value", args[index]), false)));

            commandCalled.setExecutor(getExecutor());
        }
    }

}
