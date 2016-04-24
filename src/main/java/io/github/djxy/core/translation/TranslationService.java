package io.github.djxy.core.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Samuel on 2016-04-21.
 */
public class TranslationService {

    private static final TranslationService instance = new TranslationService();

    public static final String DEFAULT_LANG = "en_US";

    public static TranslationService getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<UUID,String> playersLang = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<String> languages = new CopyOnWriteArraySet<>();

    private TranslationService(){
        languages.add(DEFAULT_LANG);
    }

    public void setPlayerLang(UUID uuid, String lang){
        playersLang.put(uuid, lang);
    }

    public String getPlayerLang(UUID uuid){
        return playersLang.containsKey(uuid)?playersLang.get(uuid): DEFAULT_LANG;
    }

    public void addLanguage(String lang){
        languages.add(lang);
    }

    public List<String> getLanguages() {
        return new ArrayList<>(languages);
    }

    public boolean containLanguage(String lang){
        return languages.contains(lang);
    }

}
