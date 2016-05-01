package io.github.djxy.core;

import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.translation.Translator;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Samuel on 2016-04-27.
 */
public interface CorePlugin {

    public String getName();

    public String getGithubApiURL();

    /**
     * Version is the name of the version
     * @return
     */
    public String getVersion();

    /**
     * If return null, no check for updates
     * @return
     */
    public Path getTranslationPath();

    public Translator getTranslator();

    public void loadTranslations();

    public List<FileManager> getFileManagers(Class<? extends FileManager>... type);

    public FileManager getFileManager(String name, Class<? extends FileManager>... type);

}
