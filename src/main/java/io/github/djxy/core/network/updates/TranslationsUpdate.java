package io.github.djxy.core.network.updates;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.network.Github;
import io.github.djxy.core.repositories.FileUpdateRepository;
import io.github.djxy.core.repositories.PlayerRepository;
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

    public TranslationsUpdate(List<CorePlugin> plugins) {
        super(plugins, PlayerRepository.RECEIVE_NOTIFICATION_TRANSLATIONS);
        this.automaticDownload = false;
    }

    public TranslationsUpdate(List<CorePlugin> plugins, boolean automaticDownload) {
        super(plugins, PlayerRepository.RECEIVE_NOTIFICATION_TRANSLATIONS, true);
        this.automaticDownload = automaticDownload;
    }

    @Override
    public void check() {
        if(automaticDownload)
            run();
        else
            super.check();
    }

    @Override
    public void run() {
        for(CorePlugin corePlugin : plugins){
            if(corePlugin.getTranslationPath() != null) {
                List<Github.FileUpdate> fileUpdates = github.getTranslationUpdates(corePlugin);

                filterFilesToDownload(fileUpdates);

                if(fileUpdates.size() != 0) {
                    if (automaticDownload)
                        automaticDownload(corePlugin, fileUpdates);
                    else
                        askPlayers(corePlugin, fileUpdates);
                }
            }
        }
    }

    private void askPlayers(CorePlugin corePlugin, List<Github.FileUpdate> fileUpdates){
        HashMap<String, Object> values = new HashMap<>();
        values.put("plugin", corePlugin.getName());

        values.put("clickHereDownload", TextActions.executeCallback(e -> {
            FileUpdateRepository fur = FileUpdateRepository.getInstance();
            int nbFileToDownload = fileUpdates.size();

            for(Github.FileUpdate fileUpdate : fileUpdates) {
                if (fur.canDownloadFile(fileUpdate.getPlugin(), fileUpdate.getSha())) {
                    fur.addFileDownloaded(fileUpdate.getPlugin(), fileUpdate.getSha());
                    nbFileToDownload--;
                }
            }

            if(nbFileToDownload == 0) {
                e.sendMessage(CoreMain.getTranslatorInstance().translate(e, "notifyTranslationsStartDownload", values));

                for (Github.FileUpdate fileUpdate : fileUpdates) {
                    String content = github.downloadFileContent(fileUpdate.getDownloadUrl(), fileUpdate.getSize());
                    File f = corePlugin.getTranslationPath().resolve(fileUpdate.getName()).toFile();


                    createFile(f, content);
                }

                corePlugin.loadTranslations();

                for (Player player : players) {
                    createSyncTask(y -> {
                        player.sendMessage(CoreMain.getTranslatorInstance().translate(player, "notifyTranslationsFinishDownload", values));
                    });
                }
            }
            else
                e.sendMessage(CoreMain.getTranslatorInstance().translate(e, "notifyTranslationsAlreadyDownloaded", values));
        }));

        values.put("clickHereStop", TextActions.executeCallback(e -> {
            PlayerRepository.getInstance().setPlayerData(((Player) e).getUniqueId(), PlayerRepository.RECEIVE_NOTIFICATION_TRANSLATIONS, false);
            e.sendMessage(CoreMain.getTranslatorInstance().translate(e, "setReceiveNotificationTranslationsFalse", null));
        }));

        for (Player player : players) {
            createSyncTask(e -> {
                player.sendMessage(CoreMain.getTranslatorInstance().translate(player, "notifyNewTranslations", values));
            });
        }
    }

    private void automaticDownload(CorePlugin corePlugin, List<Github.FileUpdate> fileUpdates) {
        FileUpdateRepository fur = FileUpdateRepository.getInstance();
        File folder = corePlugin.getTranslationPath().toFile();

        if (folder.listFiles().length == 0) {
            for (Github.FileUpdate fileUpdate : fileUpdates) {
                if (corePlugin.getTranslationPath() != null) {
                    fur.addFileDownloaded(fileUpdate.getPlugin(), fileUpdate.getSha());
                    File f = corePlugin.getTranslationPath().resolve(fileUpdate.getName()).toFile();
                    createFile(f, github.downloadFileContent(fileUpdate.getDownloadUrl(), fileUpdate.getSize()));
                }
            }
        }
    }


    private void createFile(File file, String content){
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(content.getBytes("UTF-8"));

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterFilesToDownload(List<Github.FileUpdate> fileUpdates){
        FileUpdateRepository fur = FileUpdateRepository.getInstance();

        for(int i = fileUpdates.size()-1; i >= 0; i--) {
            Github.FileUpdate fileUpdate = fileUpdates.get(i);

            if (!fur.canDownloadFile(fileUpdate.getPlugin(), fileUpdate.getSha()))
                fileUpdates.remove(fileUpdate);
        }
    }

}
