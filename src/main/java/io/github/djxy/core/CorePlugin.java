package io.github.djxy.core;

import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.translation.Translator;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Samuel on 2016-04-27.
 */
public interface CorePlugin {

    public String getGithubApiURL();

    /**
     * If return null, no check for updates
     * @return
     */
    public Path getTranslationPath();

    public Translator getTranslator();

    public void loadTranslations();

    public List<FileManager> getFileManagers(Class<? extends FileManager>... type);

    public FileManager getFileManager(String name, Class<? extends FileManager>... type);

    default public String getId() {
        return getClass().getDeclaredAnnotation(Plugin.class).id();
    }

    default String getName() {
        return getClass().getDeclaredAnnotation(Plugin.class).name();
    }

    default String getVersion() {
        return getClass().getDeclaredAnnotation(Plugin.class).version();
    }
}
