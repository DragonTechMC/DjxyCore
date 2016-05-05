package io.github.djxy.core.translation;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-04-21.
 */
public class Translator {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> translations = new ConcurrentHashMap<>();//Code,Lang,Translation
    private Text prefix = Text.of();

    public Translator() {
    }

    public Translator(Text prefix) {
        this.prefix = prefix;
    }

    public void setPrefix(Text prefix) {
        this.prefix = prefix;
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
            return translations.get(language).containsKey(code)?translations.get(language).get(code):translations.get(TranslationService.DEFAULT_LANGUAGE).get(code);
        else
            return translations.get(TranslationService.DEFAULT_LANGUAGE).get(code);
    }

    public boolean containTranslation(String language, String code){
        return translations.containsKey(language) && translations.get(language).containsKey(code);
    }

    public Text translate(String language, String code, Map<String,Object> values){
        return translate(language, code, values, true);
    }

    public Text translate(CommandSource source, String code, Map<String,Object> values){
        return translate(source, code, values, true);
    }

    public Text translate(String language, String code, Map<String,Object> values, boolean displayPrefix){
        String translation = getTranslation(language, code);

        if(!translation.isEmpty()) {
            Text text = Text.of();
            int startIndex = 0;
            int index;

            while(startIndex != translation.length() && (index = translation.substring(startIndex).indexOf('{')) != -1){
                index = index+startIndex;
                int i = startIndex + translation.substring(startIndex).indexOf('}');
                String var = translation.substring(index+1, i);

                text = text.concat(transformText(translation.substring(startIndex, index)));

                if(var.startsWith("click")){
                    TextAction action = values.containsKey(var)? (TextAction) values.get(var) :TextActions.executeCallback(e->{});
                    String click = containTranslation(language, var)?getTranslation(language, var):getTranslation(language, "clickHere");
                    text = text.concat(transformClick(click, action));
                }
                else {
                    Object value = values.containsKey(var)?values.get(var):"{"+var+"}";
                    text = value instanceof Text?text.concat((Text) value).concat(Text.of(TextColors.RESET, TextStyles.RESET)):text.concat(transformVariable(value.toString()));
                }

                startIndex = i+1;
            }

            text = text.concat(transformText(translation.substring(startIndex, translation.length())));

            return displayPrefix?prefix.concat(text):text;
        }
        else
            return Text.of();
    }

    public Text translate(CommandSource source, String code, Map<String,Object> values, boolean displayPrefix){
        if(source instanceof Player)
            return translate(TranslationService.getInstance().getPlayerLanguage(((Player) source).getUniqueId()), code, values, displayPrefix);
        else
            return translate(TranslationService.DEFAULT_LANGUAGE, code, values, displayPrefix);
    }

    public Text transformText(String text){
        return Text.of(text);
    }

    public Text transformVariable(String text){
        return Text.of(TextColors.YELLOW, text, TextColors.RESET);
    }

    public Text transformClick(String text, TextAction action){
        return Text.of(TextColors.RED, TextStyles.UNDERLINE, action, text, TextColors.RESET, TextStyles.UNDERLINE);
    }

}
