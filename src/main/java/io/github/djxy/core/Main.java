package io.github.djxy.core;

import com.google.inject.Inject;
import io.github.djxy.core.commands.Command;
import io.github.djxy.core.commands.executors.SetPlayerLanguage;
import io.github.djxy.core.commands.nodes.ChoiceNode;
import io.github.djxy.core.commands.nodes.Node;
import io.github.djxy.core.commands.nodes.arguments.LanguageNode;
import io.github.djxy.core.files.fileManagers.PlayerRepositoryFile;
import io.github.djxy.core.files.fileManagers.TranslationsFile;
import io.github.djxy.core.repositories.PlayerRepository;
import io.github.djxy.core.translation.TranslationService;
import io.github.djxy.core.translation.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by Samuel on 2016-04-23.
 */
@Plugin(id = "djxycore", name = "Djxy Core", version = "1.0")
public class Main {

    private static Translator translatorInstance;

    public static Translator getTranslatorInstance(){
        return translatorInstance;
    }

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path path;

    private Translator translator;
    private PlayerRepositoryFile playerRepositoryFile;

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent event) {
        playerRepositoryFile = new PlayerRepositoryFile(path.getParent());
        try {
            playerRepositoryFile.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Path languagesPath = path.getParent().resolve("languages");
        File languagesFolder = languagesPath.toFile();
        translator = new Translator();
        translatorInstance = translator;

        for(File language : languagesFolder.listFiles()) {
            try {
                new TranslationsFile(languagesPath, language.getName().substring(0, language.getName().indexOf('.')), translator).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Sponge.getCommandManager().register(this, new Command(createCommandLanguage()), "language");
    }

    public Node createCommandLanguage(){
        return new ChoiceNode("")
                .addNode(new LanguageNode("set", "language")
                        .setExecutor(new SetPlayerLanguage()));
    }

    @Listener
    public void onGameStoppedServerEvent(GameStoppedServerEvent event) {
        try {
            playerRepositoryFile.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Login event){
        PlayerRepository.getInstance().createPlayerIfNotExist(event.getProfile().getUniqueId());
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event){
        PlayerRepository.getInstance().setPlayerName(event.getTargetEntity().getUniqueId(), event.getTargetEntity().getName());

        if (!TranslationService.getInstance().hasPlayerLanguage(event.getTargetEntity().getUniqueId()))
            TranslationService.getInstance().setPlayerLanguage(event.getTargetEntity().getUniqueId(), TranslationService.DEFAULT_LANGUAGE);

        HashMap<String,Object> values = new HashMap<>();
        values.put("playerName", event.getTargetEntity().getName());

        event.getTargetEntity().sendMessage(translator.translate(event.getTargetEntity().getUniqueId(), "onJoin", values));
    }

}
