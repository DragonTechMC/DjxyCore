package io.github.djxy.core.commands.executors;

import io.github.djxy.core.CoreUtil;
import io.github.djxy.core.Main;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.files.FileManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;

import java.util.Map;

/**
 * Created by Samuel on 2016-04-25.
 */
public class ReloadFileManagerExecutor extends CommandExecutor {

    public ReloadFileManagerExecutor() {
        setPermission(Permissions.RELOAD_FILE);
    }

    @Override
    public void execute(CommandSource source, Map<String, Object> values) throws CommandException {
        FileManager fileManager = (FileManager) values.get("file");

        try {
            fileManager.load();
            source.sendMessage(Main.getTranslatorInstance().translate(source, "reloadFile", CoreUtil.createMap("file", fileManager.getName())));
        } catch (Exception e) {
            source.sendMessage(Main.getTranslatorInstance().translate(source, "reloadFileError", CoreUtil.createMap("file", fileManager.getName())));
            e.printStackTrace();
        }
    }

}
