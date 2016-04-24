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

    public Map<String,String> getTranslations(String lang){
        return new HashMap<>(translations.get(lang));
    }

    /**
     * @param lang The language(en_US,fr_CA,de_DE) http://www.oracle.com/technetwork/java/javase/javase7locales-334809.html
     * @param code To retrieve the translation
     * @param translation The translation
     */
    public void setTranslation(String lang, String code, String translation){
        TranslationService.getInstance().addLang(lang);

        if(!translations.containsKey(lang))
            translations.put(lang, new ConcurrentHashMap<>());

        translations.get(lang).put(code, translation);
    }

    public String getTranslation(String lang, String code){
        if(translations.containsKey(lang))
            return translations.get(lang).containsKey(code)?translations.get(lang).get(code):"";
        else
            return "";
    }

    public Text translate(UUID playerUUID, String code, Map<String,Object> values){
        String playerLang = TranslationService.getInstance().getPlayerLang(playerUUID);
        String translation = getTranslation(playerLang, code);

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
                    text = text.concat(transformClick(getTranslation(playerLang, "CLICK_HERE"), task));
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
