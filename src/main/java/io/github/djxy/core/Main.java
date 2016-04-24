package io.github.djxy.core;

import com.google.inject.Inject;
import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.files.fileManagers.LangTranslationsFile;
import io.github.djxy.core.translation.Translator;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

/**
 * Created by Samuel on 2016-04-23.
 */
@Plugin(id = "djxycore", name = "Djxy Core", version = "1.0")
public class Main {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path path;

    private Translator translator;

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent event) throws Exception {
        translator = new Translator();

        FileManager manager = new LangTranslationsFile(path.getParent(), "en_US", translator);
        manager.load();
        manager = new LangTranslationsFile(path.getParent(), "fr_CA", translator);
        manager.load();
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event){
        
    }

}
