package io.github.djxy.core.files.fileManagers;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.translation.TranslationService;
import ninja.leaping.configurate.ConfigurationNode;

import java.nio.file.Path;

/**
 * Created by Samuel on 2016-04-24.
 */
public class CoreConfigFile extends FileManager {

    private final CoreMain corePlugin;

    public CoreConfigFile(Path folder, CoreMain corePlugin) {
        super(folder, "config");
        this.corePlugin = corePlugin;
    }

    @Override
    protected void save(ConfigurationNode root) {
        root.getNode("default", "language").setValue(TranslationService.DEFAULT_LANGUAGE);
        root.getNode("update", "interval").setValue(corePlugin.getIntervalUpdate());
    }

    @Override
    protected void load(ConfigurationNode root) {
        TranslationService.DEFAULT_LANGUAGE = root.getNode("default", "language").getString("en_US");
        corePlugin.setIntervalUpdate(root.getNode("update", "interval").getInt(1));
    }

}
