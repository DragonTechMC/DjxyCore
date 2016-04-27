package io.github.djxy.core;

import com.google.inject.Inject;
import io.github.djxy.core.commands.Command;
import io.github.djxy.core.commands.executors.*;
import io.github.djxy.core.commands.nodes.ChoiceNode;
import io.github.djxy.core.commands.nodes.Node;
import io.github.djxy.core.commands.nodes.arguments.FileManagerNode;
import io.github.djxy.core.commands.nodes.arguments.LanguageNode;
import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.files.fileManagers.ConfigFile;
import io.github.djxy.core.files.fileManagers.PlayerRepositoryFile;
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
import org.spongepowered.api.text.serializer.TextSerializers;

import java.nio.file.Path;
import java.util.ArrayList;

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

    private Translator translator = new Translator(TextSerializers.FORMATTING_CODE.deserialize("&f[&6Djxy&l&4Core&r&f] "));
    private PlayerRepositoryFile playerRepositoryFile;
    private ConfigFile configFile;
    private ArrayList<FileManager> translationsFiles;

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent event) {
        CoreUtil.loadFileManagers((configFile = new ConfigFile(path.getParent())), (playerRepositoryFile = new PlayerRepositoryFile(path.getParent())));

        translationsFiles = CoreUtil.loadTranslationFiles(path.getParent().resolve("translations"), (translatorInstance = translator));

        Sponge.getCommandManager().register(this, new Command(createCommandLanguage()), "language");
        Sponge.getCommandManager().register(this, new Command(createCommandTranslation()), "translation");
    }

    @Listener
    public void onGameStoppedServerEvent(GameStoppedServerEvent event) {
        CoreUtil.saveFileManagers(configFile, playerRepositoryFile);
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Login event){
        PlayerRepository.getInstance().createPlayerIfNotExist(event.getProfile().getUniqueId());
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event){
        PlayerRepository.getInstance().setPlayerData(event.getTargetEntity().getUniqueId(), "name", event.getTargetEntity().getName());

        if (!TranslationService.getInstance().hasPlayerLanguage(event.getTargetEntity().getUniqueId()))
            TranslationService.getInstance().setPlayerLanguage(event.getTargetEntity().getUniqueId(), TranslationService.DEFAULT_LANGUAGE);
    }

    public Node createCommandLanguage(){
        return new ChoiceNode("")
                .setExecutor(new PlayerGetLanguageExecutor())
                .addNode(new LanguageNode("set", "language")
                        .setExecutor(new PlayerSetLanguageExecutor()))
                .addNode(new ChoiceNode("default")
                        .setExecutor(new DefaultGetLanguageExecutor())
                        .addNode(new LanguageNode("set", "language")
                                .setExecutor(new DefaultSetLanguageExecutor())));
    }

    public Node createCommandTranslation(){
        return new ChoiceNode("")
                .addNode(new ChoiceNode("reload")
                        .setExecutor(new ReloadFileManagersExecutor(translationsFiles))
                        .addNode(new FileManagerNode("file", translationsFiles)
                                .setExecutor(new ReloadFileManagerExecutor())));
    }

}
