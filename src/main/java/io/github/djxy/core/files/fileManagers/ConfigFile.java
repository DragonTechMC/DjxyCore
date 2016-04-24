package io.github.djxy.core.files.fileManagers;

import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.translation.TranslationService;
import ninja.leaping.configurate.ConfigurationNode;

import java.nio.file.Path;

/**
 * Created by Samuel on 2016-04-24.
 */
public class ConfigFile extends FileManager {

    public ConfigFile(Path folder) {
        super(folder, "config");
    }

    @Override
    protected void save(ConfigurationNode root) {
        root.getNode("default", "language").setValue(TranslationService.DEFAULT_LANGUAGE);
    }

    @Override
    protected void load(ConfigurationNode root) {
        TranslationService.DEFAULT_LANGUAGE = root.getNode("default", "language").getString("en_US");
    }

}
