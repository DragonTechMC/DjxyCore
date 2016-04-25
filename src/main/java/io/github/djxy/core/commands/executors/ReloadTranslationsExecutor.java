package io.github.djxy.core.commands.executors;

import io.github.djxy.core.Main;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.files.fileManagers.TranslationsFile;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;

import java.util.List;
import java.util.Map;

/**
 * Created by Samuel on 2016-04-25.
 */
public class ReloadTranslationsExecutor extends CommandExecutor {

    private final List<TranslationsFile> translationsFiles;

    public ReloadTranslationsExecutor(List<TranslationsFile> translationsFiles) {
        this.translationsFiles = translationsFiles;
        setPermission(Permissions.RELOAD_TRANSLATIONS);
    }

    @Override
    public void execute(CommandSource source, Map<String, Object> values) throws CommandException {
        source.sendMessage(Main.getTranslatorInstance().translate(source, "reloadTranslationFiles", EMPTY, false));

        for(TranslationsFile translationsFile : translationsFiles) {
            try {
                translationsFile.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
