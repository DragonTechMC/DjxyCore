package io.github.djxy.core;

import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.files.fileManagers.TranslationsFile;
import io.github.djxy.core.translation.Translator;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuel on 2016-04-26.
 */
public class CoreUtil {

    public static ArrayList<FileManager> loadTranslationFiles(Path translationsPath, Translator translator){
        ArrayList<FileManager> translationsFiles = new ArrayList<>();

        for(File language : translationsPath.toFile().listFiles()) {
            try {
                TranslationsFile translationsFile = new TranslationsFile(translationsPath, language.getName().substring(0, language.getName().indexOf('.')), translator);

                translationsFile.load();
                translationsFiles.add(translationsFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return translationsFiles;
    }

    public static void loadFileManagers(FileManager... fileManagers){
        for(FileManager manager : fileManagers){
            try {
                manager.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveFileManagers(FileManager... fileManagers){
        for(FileManager manager : fileManagers){
            try {
                manager.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String,Object> createMap(String key, Object value){
        HashMap<String,Object> values = new HashMap<>();
        values.put(key, value);

        return values;
    }

    public static Map<String,Object> createMap(String key1, Object value1, String key2, Object value2){
        HashMap<String,Object> values = new HashMap<>();
        values.put(key1, value1);
        values.put(key2, value2);

        return values;
    }

}
