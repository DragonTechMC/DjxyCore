package io.github.djxy.core.files.fileManagers;

import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.translation.Translator;
import ninja.leaping.configurate.ConfigurationNode;

import java.nio.file.Path;
import java.util.Map;

/**
 * Created by Samuel on 2016-04-23.
 */
public class TranslationsFile extends FileManager {

    private final Translator translator;

    public TranslationsFile(Path folder, String name, Translator translator) {
        super(folder, name);
        this.translator = translator;
    }

    @Override
    protected void save(ConfigurationNode root) {

    }

    @Override
    protected void load(ConfigurationNode root) {
        Map<Object, ConfigurationNode> map = (Map<Object, ConfigurationNode>) root.getChildrenMap();

        for(Object code : map.keySet())
            translator.setTranslation(getName(), code.toString(), map.get(code).getString());
    }

}
