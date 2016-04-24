package io.github.djxy.core.files;

/**
 * Created by Samuel on 2016-04-02.
 */
public enum FileFormat {

    YAML("yml"),
    JSON("json"),
    HOCON("hocon");

    private final String extension;

    FileFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
