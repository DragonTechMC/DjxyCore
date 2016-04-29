package io.github.djxy.core.network.updates;

import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.network.Github;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.action.TextActions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Samuel on 2016-04-29.
 */
public class TranslationsUpdate extends Update {

    private final boolean automaticDownload;

    public TranslationsUpdate(CorePlugin plugin) {
        super(plugin);
        this.automaticDownload = false;
    }

    public TranslationsUpdate(CorePlugin plugin, boolean automaticDownload) {
        super(plugin);
        this.automaticDownload = automaticDownload;
    }

    @Override
    public void run() {
        for(CorePlugin corePlugin : plugins){
            List<Github.FileUpdate> fileUpdates = github.getTranslationUpdates(corePlugin);

            for(Github.FileUpdate fileUpdate : fileUpdates){
                if(automaticDownload)
                    automaticDownload(corePlugin, fileUpdate);
                else
                    askPlayers(corePlugin, fileUpdate);
            }
        }
    }

    private void askPlayers(CorePlugin corePlugin, Github.FileUpdate fileUpdate){
        HashMap<String,Object> values = new HashMap<>();
        values.put("file", fileUpdate.getName());
        values.put("clickHereDownload", TextActions.executeCallback(e -> {
            String content = github.downloadFileContent(fileUpdate.getDownloadUrl(), fileUpdate.getSize());
            File f = corePlugin.getTranslationPath().resolve(fileUpdate.getName()).toFile();

            createFile(f, content);
        }));
        values.put("clickHereStop", TextActions.executeCallback(e -> {

        }));

        for(Player player : players) {
            createSyncTask(e -> {
                player.sendMessage(plugin.getTranslator().translate(player, "notifyNewRelease", values));
            });
        }
    }

    private void automaticDownload(CorePlugin corePlugin, Github.FileUpdate fileUpdate){
        if(corePlugin.getTranslationPath() != null){
            File folder = corePlugin.getTranslationPath().toFile();

            if(folder.listFiles().length == 0) {
                File f = corePlugin.getTranslationPath().resolve(fileUpdate.getName()).toFile();
                createFile(f, github.downloadFileContent(fileUpdate.getDownloadUrl(), fileUpdate.getSize()));
            }
        }
    }

    private void createFile(File file, String content){
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(content.getBytes());

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
