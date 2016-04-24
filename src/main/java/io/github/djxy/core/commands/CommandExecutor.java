package io.github.djxy.core.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuel on 2016-04-05.
 */
public abstract class CommandExecutor {

    public static final HashMap<String,Object> EMPTY = new HashMap<>();
    public static final TextColor WARNING_COLOR = TextColors.RED;
    public static final TextColor RESET_COLOR = TextColors.RESET;

    private String permission;

    abstract public void execute(CommandSource source, Map<String,Object> values) throws CommandException ;

    public String getPermission() {
        return permission;
    }

    public CommandExecutor setPermission(String permission) {
        this.permission = permission;
        return this;
    }

}
