package io.github.djxy.core.commands.executors;

import io.github.djxy.core.Main;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.translation.TranslationService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;

/**
 * Created by Samuel on 2016-04-24.
 */
public class SetPlayerLanguage extends CommandExecutor {

    @Override
    public void execute(CommandSource source, Map<String, Object> values) throws CommandException {
        if(source instanceof Player){
            TranslationService.getInstance().setPlayerLanguage(((Player) source).getUniqueId(), (String) values.get("language"));
            source.sendMessage(Main.getTranslatorInstance().translate(((Player) source).getUniqueId(), "onSetPlayerLanguage", EMPTY));
        }
    }

}
