package io.github.djxy.core.commands.executors;

import io.github.djxy.core.Main;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.translation.TranslationService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;

import java.util.Map;

/**
 * Created by Samuel on 2016-04-24.
 */
public class DefaultGetLanguage extends CommandExecutor {

    @Override
    public void execute(CommandSource source, Map<String, Object> values) throws CommandException {
        source.sendMessage(Main.getTranslatorInstance().translate(TranslationService.DEFAULT_LANGUAGE, "getDefaultLanguage", EMPTY));
    }
}
