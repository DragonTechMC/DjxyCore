package io.github.djxy.core.commands.nodes;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.commands.CommandExecutor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Samuel on 2016-04-09.
 */
public abstract class Node {

    protected static final List<String> EMPTY_LIST = new ArrayList<>();
    protected static final Map<String,Object> EMPTY_MAP = new HashMap<>();

    protected final String alias;
    private CommandExecutor executor;

    abstract public Node addNode(Node node);
    abstract public Node getNode(String node);
    abstract protected List<String> complete(String complete);

    public Node(String alias) {
        this.alias = alias;
    }

    public void executeCommand(CommandSource source, String[] args) throws CommandException {
        CommandCalled commandCalled = new CommandCalled();

        createCommandCalled(commandCalled, source, args, 0);

        if(commandCalled.executor != null) {
            if (commandCalled.executor.getPermission() == null || source.hasPermission(commandCalled.executor.getPermission()))
                commandCalled.executor.execute(source, commandCalled.values);
            else
                throw new CommandException(Text.of(TextColors.RED).concat(CoreMain.getTranslatorInstance().translate(source, "commandRequirePermission", EMPTY_MAP, false)));
        }
        else
            throw new CommandException(Text.of(TextColors.RED).concat(CoreMain.getTranslatorInstance().translate(source, "commandInvalid", EMPTY_MAP, false)));
    }

    public void createCommandCalled(CommandCalled commandCalled, CommandSource source, String[] args, int index) throws CommandException {
        if(index < args.length){
            Node next = getNode(args[index]);

            if(next != null)
                next.createCommandCalled(commandCalled, source, args, index+1);
        }
        else {
            commandCalled.setExecutor(getExecutor());
        }
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public Node setExecutor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public List<String> getSuggestion(String[] args){
        if(args.length > 0)
            return getSuggestion(args, 0);
        else
            return complete("");
    }

    public List<String> getSuggestion(String[] args, int index){
        if(index+1 < args.length){
            Node next = getNode(args[index]);

            if(next != null)
                return next.getSuggestion(args, index+1);
            else
                return EMPTY_LIST;
        }
        else
            return complete(args[index].toLowerCase());
    }

    protected class CommandCalled {

        private final HashMap<String, Object> values;
        private CommandExecutor executor;

        public CommandCalled() {
            this.values = new HashMap<>();
        }

        public void setExecutor(CommandExecutor executor) {
            this.executor = executor;
        }

        public void addValue(String name, Object value){
            values.put(name, value);
        }

    }

}
