package io.github.djxy.core.commands.executors;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.repositories.FileUpdateRepository;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.action.TextActions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuel on 2016-05-03.
 */
public class FileUpdateExecutor extends CommandExecutor {

    public FileUpdateExecutor() {
        setPermission(Permissions.UPDATE);
    }

    @Override
    public void execute(CommandSource source, Map<String, Object> values) throws CommandException {
        FileUpdateRepository fur = FileUpdateRepository.getInstance();

        if(fur.hasUpdate()){
            source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "fileUpdateHeader", null));
            for(String plugin : fur.getPlugins()){
                Collection<FileUpdateRepository.FileUpdate> fileUpdates = fur.getFileUpdates(plugin);

                if(!fileUpdates.isEmpty()) {
                    HashMap<String, Object> map = new HashMap<>();

                    map.put("plugin", plugin);
                    map.put("nbFile", fileUpdates.size());
                    map.put("clickDownload", TextActions.executeCallback(e -> {
                        int fileToDownload = fileUpdates.size();

                        for (FileUpdateRepository.FileUpdate fileUpdate : fileUpdates) {
                            if (fileUpdate.canDownload()) {
                                fileToDownload--;
                                fileUpdate.download();
                            }
                        }

                        if (fileToDownload != fileUpdates.size())
                            source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "fileUpdateDownloadFinished", map));
                        else
                            source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "fileUpdateDownloadNoFile", map));
                    }));

                    source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "fileUpdateRow", map));
                }
            }
        }
        else
            source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "noFileUpdate", null));
    }

}
