package io.github.djxy.core.files;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Samuel on 2016-04-02.
 */
abstract public class FileManager {

    private final Path folder;
    private final String name;
    private FileFormat format = FileFormat.YAML;
    private FileFormat lastFormat = FileFormat.YAML;
    private boolean loadingFailed = false;

    abstract protected void save(ConfigurationNode root);
    abstract protected void load(ConfigurationNode root);

    public FileManager(Path folder, String name) {
        this.folder = folder;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public FileFormat getFormat() {
        return format;
    }

    public void setFormat(FileFormat format) {
        this.format = format;
    }

    public void load() throws Exception {
        try {
            if(isFileExist(FileFormat.HOCON))
                load(HoconConfigurationLoader.builder().setDefaultOptions(ConfigurationOptions.defaults()).setFile(getFile(format = (lastFormat = FileFormat.HOCON))).build().load());
            else if(isFileExist(FileFormat.JSON))
                load(GsonConfigurationLoader.builder().setDefaultOptions(ConfigurationOptions.defaults()).setFile(getFile(format = (lastFormat = FileFormat.JSON))).build().load());
            else if(isFileExist(FileFormat.YAML))
                load(YAMLConfigurationLoader.builder().setDefaultOptions(ConfigurationOptions.defaults()).setFile(getFile(format = (lastFormat = FileFormat.YAML))).build().load());

            loadingFailed = false;
        } catch (Exception e){
            loadingFailed = true;
            throw e;
        }
    }

    public void save() throws Exception {
        if(!loadingFailed) {
            ConfigurationLoader loader = null;
            ConfigurationNode root;

            if (lastFormat != format) {
                getFile(lastFormat).delete();
                getFile(format).createNewFile();
            }
            switch (format) {
                case HOCON:
                    loader = HoconConfigurationLoader.builder().setDefaultOptions(ConfigurationOptions.defaults()).setFile(getFile(FileFormat.HOCON)).build();
                    break;
                case JSON:
                    loader = GsonConfigurationLoader.builder().setDefaultOptions(ConfigurationOptions.defaults()).setFile(getFile(FileFormat.JSON)).build();
                    break;
                case YAML:
                    loader = YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(getFile(FileFormat.YAML)).build();
                    break;
            }

            root = loader.createEmptyNode();
            save(root);
            loader.save(root);
        }
    }

    private File getFile(FileFormat format){
        return folder.resolve(name+"."+format.getExtension()).toFile();
    }

    private boolean isFileExist(FileFormat format){
        return folder.resolve(name+"."+format.getExtension()).toFile().exists();
    }

}
