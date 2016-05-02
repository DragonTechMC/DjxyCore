package io.github.djxy.core.network.updates;

import io.github.djxy.core.CoreMain;

/**
 * Created by Samuel on 2016-04-29.
 */
public class TranslationsUpdate extends Update {

    public TranslationsUpdate(boolean async) {
        super(async);
    }

    @Override
    public void run() {
        CoreMain.getInstance().getCorePlugins().stream().filter(corePlugin -> corePlugin.getTranslationPath() != null).forEach(github::checkTranslationUpdates);
    }

}
