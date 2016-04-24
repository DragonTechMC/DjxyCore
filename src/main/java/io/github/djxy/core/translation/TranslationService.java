package io.github.djxy.core.translation;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-04-21.
 */
public class TranslationService {

    private static final TranslationService instance = new TranslationService();

    public static final String defaultLang = "en_US";

    public static TranslationService getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<UUID,String> playersLang = new ConcurrentHashMap<>();

    private TranslationService(){}

    public void setPlayerLang(UUID uuid, String lang){
        playersLang.put(uuid, lang);
    }

    public String getPlayerLang(UUID uuid){
        return playersLang.containsKey(uuid)?playersLang.get(uuid):defaultLang;
    }

}
