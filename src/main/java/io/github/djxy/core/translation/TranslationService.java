package io.github.djxy.core.translation;

import io.github.djxy.core.repositories.PlayerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Samuel on 2016-04-21.
 */
public class TranslationService {

    public static String DEFAULT_LANGUAGE = "en_US";
    private static final TranslationService instance = new TranslationService();

    public static TranslationService getInstance() {
        return instance;
    }

    private final CopyOnWriteArraySet<String> languages = new CopyOnWriteArraySet<>();

    private TranslationService(){
        languages.add(DEFAULT_LANGUAGE);
    }

    public boolean hasPlayerLanguage(UUID uuid){
        return PlayerRepository.getInstance().hasPlayerData(uuid, "language");
    }

    public void setPlayerLanguage(UUID uuid, String lang){
        PlayerRepository.getInstance().setPlayerData(uuid, "language", lang);
    }

    public String getPlayerLanguage(UUID uuid){
        return (String) PlayerRepository.getInstance().getPlayerData(uuid, "language");
    }

    public void addLanguage(String lang){
        languages.add(lang);
    }

    public List<String> getLanguages() {
        System.out.println(new ArrayList<>(languages));
        return new ArrayList<>(languages);
    }

    public boolean containLanguage(String lang){
        return languages.contains(lang);
    }

}
