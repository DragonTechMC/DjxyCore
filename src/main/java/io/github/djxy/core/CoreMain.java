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
import io.github.djxy.core.files.fileManagers.TranslationsFile;
import io.github.djxy.core.network.Github;
import io.github.djxy.core.network.Metrics;
import io.github.djxy.core.network.updates.PluginsUpdate;
import io.github.djxy.core.network.updates.TranslationsUpdate;
import io.github.djxy.core.network.updates.Update;
import io.github.djxy.core.repositories.PlayerRepository;
import io.github.djxy.core.translation.TranslationService;
import io.github.djxy.core.translation.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-04-23.
 */
@Plugin(id = "djxycore", name = "Djxy Core", version = "1.0")
public class CoreMain implements CorePlugin {

    private static CoreMain instance;
    private static Translator translatorInstance;

    public static CoreMain getInstance(){
        return instance;
    }

    public static Translator getTranslatorInstance(){
        return translatorInstance;
    }

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path path;

    private Translator translator = new Translator(TextSerializers.FORMATTING_CODE.deserialize("&f[&6Djxy&l&4Core&r&f] "));
    private CopyOnWriteArrayList<CorePlugin> corePlugins = new CopyOnWriteArrayList<>();
    private ArrayList<FileManager> translationsFiles;
    private PlayerRepositoryFile playerRepositoryFile;
    private ConfigFile configFile;
    private Path translationPath;
    private Github github = new Github();

    @Override
    public String getName() {
        return "DjxyCore";
    }

    @Override
    public String getGithubApiURL() {
        return "https://api.github.com/repos/djxy/DjxyCore";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public Path getTranslationPath() {
        return translationPath;
    }

    @Override
    public Translator getTranslator() {
        return translator;
    }

    @Override
    public List<FileManager> getFileManagers(Class<? extends FileManager>... type) {
        return null;
    }

    @Override
    public FileManager getFileManager(String name, Class<? extends FileManager>... type) {
        return null;
    }

    @Listener
    public void onGameConstructionEvent(GameConstructionEvent event){
        instance = this;
    }

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent event) {
        CoreUtil.loadFileManagers((configFile = new ConfigFile(path.getParent())), (playerRepositoryFile = new PlayerRepositoryFile(path.getParent())));
        translationsFiles = CoreUtil.loadTranslationFiles((translationPath = path.getParent().resolve("translations")), (translatorInstance = translator));

        Sponge.getCommandManager().register(this, new Command(createCommandLanguage()), "language");
        Sponge.getCommandManager().register(this, new Command(createCommandTranslation()), "translation");
        addCorePlugin(new CorePlugin() {
            @Override
            public String getName() {
                return "DjxyCore";
            }

            @Override
            public String getGithubApiURL() {
                return "https://api.github.com/repos/djxy/DjxyCore";
            }

            @Override
            public String getVersion() {
                return "-1-1";
            }

            @Override
            public Path getTranslationPath() {
                return translationPath;
            }

            @Override
            public Translator getTranslator() {
                return translator;
            }

            @Override
            public List<FileManager> getFileManagers(Class<? extends FileManager>... type) {
                return null;
            }

            @Override
            public FileManager getFileManager(String name, Class<? extends FileManager>... type) {
                return null;
            }

        });
        initMetrics();
        new TranslationsUpdate(this, true).run();
    }

    public void checkPluginsUpdate(){
        checkUpdate(new PluginsUpdate(this));
    }

    public void checkTranslationsUpdate(){
        checkUpdate(new TranslationsUpdate(this));
    }

    private void checkUpdate(Update update){
        Optional<PermissionService> opt = Sponge.getServiceManager().provide(PermissionService.class);

        if(opt.isPresent()){
            PermissionService permissionService = opt.get();
            Set<Subject> subjects = permissionService.getUserSubjects().getAllWithPermission(Permissions.NOTIFY_UPDATE).keySet();
            List<Player> players = new ArrayList<>();

            for(Subject subject : subjects){
                try{
                    UUID uuid = UUID.fromString(subject.getIdentifier());
                    players.add(Sponge.getServer().getPlayer(uuid).get());
                }catch (Exception e){}
            }

            if(players.size() != 0) {
                update.setPlayersToNotify(players);
                update.setPluginsToCheck((List<CorePlugin>) corePlugins.clone());

                new Thread(update).start();
            }
        }
    }

    public void addCorePlugin(CorePlugin corePlugin){
        corePlugins.add(corePlugin);
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

        checkPluginsUpdate();
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
                        .setExecutor(new ReloadFileManagersExecutor(this, TranslationsFile.class))
                        .addNode(new FileManagerNode("file", this, TranslationsFile.class)
                                .setExecutor(new ReloadFileManagerExecutor())));
    }

    private void initMetrics(){
        try {
            CoreMain plugin = this;
            Metrics metrics = new Metrics(Sponge.getGame(), new PluginContainer() {
                @Override
                public String getId() {
                    return plugin.getName();
                }

                @Override
                public String getUnqualifiedId() {
                    return plugin.getName();
                }

                @Override
                public Optional<?> getInstance() {
                    return Optional.of(plugin);
                }

                @Override
                public Optional<String> getVersion() {
                    return Optional.of(plugin.getVersion());
                }
            });
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
