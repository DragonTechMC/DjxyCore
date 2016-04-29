package io.github.djxy.core.commands.executors;

import io.github.djxy.core.CoreUtil;
import io.github.djxy.core.CoreMain;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.files.FileFormat;
import io.github.djxy.core.files.FileManager;
import org.spongepowered.api.command.CommandSource;

import java.util.Map;

/**
 * Created by Samuel on 2016-04-09.
 */
public class FileSetFormatExecutor extends CommandExecutor {

    public FileSetFormatExecutor() {
        setPermission(Permissions.FILE_SET_FORMAT);
    }

    @Override
    public void execute(CommandSource source, Map<String, Object> values) {
        FileManager fileManager = (FileManager) values.get("file");

        FileFormat format = (FileFormat) values.get("format");
        source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "setFileFormat", CoreUtil.createMap("file", fileManager.getName(), "format", format.name())));
        fileManager.setFormat(format);
    }

}
