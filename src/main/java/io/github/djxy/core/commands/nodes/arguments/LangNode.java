package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.commands.nodes.ArgumentNode;
import io.github.djxy.core.translation.TranslationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-04-08.
 */
public class LangNode extends ArgumentNode {

    public LangNode(String alias, String name) {
        super(alias, name);
    }

    public LangNode(String alias) {
        super(alias);
    }

    @Override
    public Object getValue(String arg) {
        return TranslationService.getInstance().containLang(arg)?arg:null;
    }

    @Override
    public List<String> complete(String complete) {
        List<String> values = new ArrayList<>();

        for(String lang : TranslationService.getInstance().getLangs())
            if(lang.toLowerCase().startsWith(complete))
                values.add(lang);

        return values;
    }

}
