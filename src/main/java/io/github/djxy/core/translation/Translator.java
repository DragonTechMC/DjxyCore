package io.github.djxy.core.translation;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-04-21.
 */
public class Translator {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> translations = new ConcurrentHashMap<>();//Code,Lang,Translation

    public Map<String,String> getTranslations(String language){
        return new HashMap<>(translations.get(language));
    }

    /**
     * @param language The language(en_US,fr_CA,de_DE) http://www.oracle.com/technetwork/java/javase/javase7locales-334809.html
     * @param code To retrieve the translation
     * @param translation The translation
     */
    public void setTranslation(String language, String code, String translation){
        TranslationService.getInstance().addLanguage(language);

        if(!translations.containsKey(language))
            translations.put(language, new ConcurrentHashMap<>());

        translations.get(language).put(code, translation);
    }

    public String getTranslation(String language, String code){
        if(translations.containsKey(language))
            return translations.get(language).containsKey(code)?translations.get(language).get(code):"";
        else
            return translations.get(TranslationService.DEFAULT_LANGUAGE).get(code);
    }

    public Text translate(String playerLanguage, String code, Map<String,Object> values){
        String translation = getTranslation(playerLanguage, code);

        if(!translation.isEmpty()) {
            Text text = Text.of();
            int startIndex = 0;
            int index;

            while(startIndex != translation.length() && (index = translation.substring(startIndex).indexOf('{')) != -1){
                index = index+startIndex;
                int i = startIndex + translation.substring(startIndex).indexOf('}');
                String var = translation.substring(index+1, i);

                text = text.concat(transformText(translation.substring(startIndex, index)));

                if(var.equals("CLICK")){
                    Consumer task = values.containsKey("CLICK")? (Consumer) values.get("CLICK") :e -> {};
                    text = text.concat(transformClick(getTranslation(playerLanguage, "CLICK_HERE"), task));
                }
                else {
                    String value = values.containsKey(var)?values.get(var).toString():"{"+var+"}";
                    text = text.concat(transformVariable(value));
                }

                startIndex = i+1;
            }

            text = text.concat(transformText(translation.substring(startIndex, translation.length())));

            return text;
        }
        else
            return Text.of();
    }

    public Text translate(UUID playerUUID, String code, Map<String,Object> values){
        return translate(TranslationService.getInstance().getPlayerLanguage(playerUUID), code, values);
    }

    public Text transformText(String text){
        return Text.of(text);
    }

    public Text transformVariable(String text){
        return Text.of(TextColors.YELLOW, text, TextColors.RESET);
    }

    public Text transformClick(String text, Consumer task){
        return Text.of(TextColors.RED, TextStyles.UNDERLINE, TextActions.executeCallback(task), text, TextColors.RESET, TextStyles.UNDERLINE);
    }

}
