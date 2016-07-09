package io.github.djxy.core.commands;

import io.github.djxy.core.commands.nodes.Node;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Samuel on 2016-04-05.
 */
public class Command implements CommandCallable {

    private final Node root;

    public Command(Node root) {
        this.root = root;
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        root.executeCommand(commandSource, getArgs(s.trim()));

        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource commandSource, String s, @Nullable Location<World> location) throws CommandException
    {
        return root.getSuggestion(getArgs(s));
    }

    @Override
    public boolean testPermission(CommandSource commandSource) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource commandSource) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource commandSource) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource commandSource) {
        return Text.of();
    }

    public static String[] getArgs(String str) {
        ArrayList<String> words = new ArrayList<>();
        StringBuilder builder = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) <= 32 && builder.length() != 0) {
                words.add(builder.toString());
                builder = new StringBuilder(str.length() - i);
            } else if (str.charAt(i) > 32)
                builder.append(str.charAt(i));
        }

        if (builder.length() != 0)
            words.add(builder.toString());

        if (str.trim().length() != 0 && str.charAt(str.length() - 1) <= 32)
            words.add("");

        return words.toArray(new String[words.size()]);
    }

}
