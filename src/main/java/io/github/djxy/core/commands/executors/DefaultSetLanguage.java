package io.github.djxy.core.commands.executors;

import io.github.djxy.core.Main;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.translation.TranslationService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;

import java.util.Map;

/**
 * Created by Samuel on 2016-04-24.
 */
public class DefaultSetLanguage extends CommandExecutor {

    public DefaultSetLanguage() {
        setPermission(Permissions.DEFAULT_SET_LANGUAGE);
    }

    @Override
    public void execute(CommandSource source, Map<String, Object> values) throws CommandException {
        TranslationService.DEFAULT_LANGUAGE = (String) values.get("language");
        source.sendMessage(Main.getTranslatorInstance().translate(source, "setDefaultLanguage", EMPTY));
    }
}
