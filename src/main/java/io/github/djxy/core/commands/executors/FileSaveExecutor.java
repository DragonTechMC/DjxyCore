package io.github.djxy.core.commands.executors;

import io.github.djxy.core.CoreUtil;
import io.github.djxy.core.Main;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.files.FileManager;
import org.spongepowered.api.command.CommandSource;

import java.util.Map;

/**
 * Created by Samuel on 2016-04-10.
 */
public class FileSaveExecutor extends CommandExecutor {

    public FileSaveExecutor() {
        setPermission(Permissions.FILE_SAVE);
    }

    @Override
    public void execute(CommandSource source, Map<String, Object> values) {
        FileManager fileManager = (FileManager) values.get("file");

        try {
            fileManager.save();
            source.sendMessage(Main.getTranslatorInstance().translate(source, "saveFile", CoreUtil.createMap("file", fileManager.getName())));
        } catch (Exception e) {
            source.sendMessage(Main.getTranslatorInstance().translate(source, "saveFileError", CoreUtil.createMap("file", fileManager.getName())));
            e.printStackTrace();
        }
    }

}
