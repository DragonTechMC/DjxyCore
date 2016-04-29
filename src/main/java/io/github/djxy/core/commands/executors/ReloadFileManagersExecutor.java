package io.github.djxy.core.commands.executors;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.CoreUtil;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.files.FileManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;

import java.util.Map;

/**
 * Created by Samuel on 2016-04-25.
 */
public class ReloadFileManagersExecutor extends CommandExecutor {

    private final CorePlugin corePlugin;
    private final Class<? extends FileManager> types[];

    public ReloadFileManagersExecutor(CorePlugin plugin, Class<? extends FileManager>... types) {
        this.corePlugin = plugin;
        this.types = types;
        setPermission(Permissions.RELOAD_FILE);
    }

    @Override
    public void execute(CommandSource source, Map<String, Object> values) throws CommandException {

        for(FileManager fileManager : corePlugin.getFileManagers(types)) {
            try {
                fileManager.load();
            } catch (Exception e) {
                source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "reloadFileError", CoreUtil.createMap("file", fileManager.getName())));
                e.printStackTrace();
            }
        }
        source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "reloadFiles", EMPTY));
    }

}
