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
    private final CopyOnWriteArraySet<String> langs = new CopyOnWriteArraySet<>();

    private TranslationService(){
        langs.add(DEFAULT_LANG);
    }

    public void setPlayerLang(UUID uuid, String lang){
        playersLang.put(uuid, lang);
    }

    public String getPlayerLang(UUID uuid){
        return playersLang.containsKey(uuid)?playersLang.get(uuid): DEFAULT_LANG;
    }

    public void addLang(String lang){
        langs.add(lang);
    }

    public List<String> getLangs() {
        return new ArrayList<>(langs);
    }

    public boolean containLang(String lang){
        return langs.contains(lang);
    }

}
