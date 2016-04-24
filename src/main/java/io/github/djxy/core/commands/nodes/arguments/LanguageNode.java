package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.commands.nodes.ArgumentNode;
import io.github.djxy.core.translation.TranslationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-04-08.
 */
public class LanguageNode extends ArgumentNode {

    public LanguageNode(String alias, String name) {
        super(alias, name);
    }

    public LanguageNode(String alias) {
        super(alias);
    }

    @Override
    public Object getValue(String arg) {
        return TranslationService.getInstance().containLanguage(arg)?arg:null;
    }

    @Override
    public List<String> complete(String complete) {
        List<String> values = new ArrayList<>();

        for(String language : TranslationService.getInstance().getLanguages())
            if(language.toLowerCase().startsWith(complete))
                values.add(language);

        return values;
    }

}
