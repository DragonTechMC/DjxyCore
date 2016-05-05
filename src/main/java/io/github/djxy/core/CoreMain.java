package io.github.djxy.core;

import com.google.inject.Inject;
import io.github.djxy.core.commands.Command;
import io.github.djxy.core.commands.executors.*;
import io.github.djxy.core.commands.nodes.ChoiceNode;
import io.github.djxy.core.commands.nodes.Node;
import io.github.djxy.core.commands.nodes.arguments.BooleanNode;
import io.github.djxy.core.commands.nodes.arguments.FileManagerNode;
import io.github.djxy.core.commands.nodes.arguments.LanguageNode;
import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.files.fileManagers.CoreConfigFile;
import io.github.djxy.core.files.fileManagers.FileUpdateRepositoryFile;
import io.github.djxy.core.files.fileManagers.PlayerRepositoryFile;
import io.github.djxy.core.files.fileManagers.TranslationsFile;
import io.github.djxy.core.network.Metrics;
import io.github.djxy.core.network.updates.PluginsUpdate;
import io.github.djxy.core.network.updates.TranslationsUpdate;
import io.github.djxy.core.repositories.FileUpdateRepository;
import io.github.djxy.core.repositories.PlayerRepository;
import io.github.djxy.core.repositories.PluginUpdateRepository;
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
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Samuel on 2016-04-23.
 */
@Plugin(id = "djxycore", name = "DjxyCore", version = "v1.0")
public class CoreMain implements CorePlugin {

    private static CoreMain instance;

    public static CoreMain getInstance() {
        return instance;
    }

    public static Translator getTranslatorInstance(){
        return instance.getTranslator();
    }

    @Inject @DefaultConfig(sharedRoot = false) private Path path;
    private Translator translator = new Translator(TextSerializers.FORMATTING_CODE.deserialize("&f[&6Djxy&l&4Core&r&f] "));
    private CopyOnWriteArrayList<CorePlugin> corePlugins = new CopyOnWriteArrayList<>();
    private FileUpdateRepositoryFile fileUpdateRepositoryFile;
    private ArrayList<FileManager> translationsFiles;
    private PlayerRepositoryFile playerRepositoryFile;
    private CoreConfigFile configFile;
    private int intervalUpdate = 1;
    private Task updateTask;

    public Collection<CorePlugin> getCorePlugins() {
        return (CopyOnWriteArrayList<CorePlugin>) corePlugins.clone();
    }

    @Override
    public String getGithubApiURL() {
        return "https://api.github.com/repos/djxy/DjxyCore";
    }

    @Override
    public Path getTranslationPath() {
        return path.getParent().resolve("translations");
    }

    @Override
    public Translator getTranslator() {
        return translator;
    }

    @Override
    public void loadTranslations() {
        translationsFiles = CoreUtil.loadTranslationFiles(getTranslationPath(), translator);
    }

    @Override
    public List<FileManager> getFileManagers(Class<? extends FileManager>... types) {
        List<FileManager> fileManagers = new ArrayList<>();

        for(Class<? extends FileManager> clazz : types){
            if(clazz == TranslationsFile.class) fileManagers.addAll((Collection<? extends FileManager>) translationsFiles.clone());
            else if(clazz == PlayerRepositoryFile.class) fileManagers.add(playerRepositoryFile);
            else if(clazz == CoreConfigFile.class) fileManagers.add(configFile);
            else if(clazz == FileUpdateRepositoryFile.class) fileManagers.add(fileUpdateRepositoryFile);
        }

        return fileManagers;
    }

    @Override
    public FileManager getFileManager(String name, Class<? extends FileManager>... types) {
        name = name.toLowerCase();

        for(Class<? extends FileManager> clazz : types){
            if(clazz == TranslationsFile.class) {
                for (FileManager translationsFile : translationsFiles)
                    if (translationsFile.getName().toLowerCase().equals(name))
                        return translationsFile;
            }
            else if(clazz == PlayerRepositoryFile.class && playerRepositoryFile.getName().toLowerCase().equals(name))
                return playerRepositoryFile;
            else if(clazz == CoreConfigFile.class && configFile.getName().toLowerCase().equals(name))
                return configFile;
            else if(clazz == FileUpdateRepositoryFile.class && fileUpdateRepositoryFile.getName().toLowerCase().equals(name))
                return fileUpdateRepositoryFile;
        }

        return null;
    }

    @Listener
    public void onGameConstructionEvent(GameConstructionEvent event){
        instance = this;

        loadCorePlugins();

        corePlugins.stream().filter(corePlugin -> corePlugin.getTranslationPath() != null).forEach(corePlugin -> corePlugin.getTranslationPath().toFile().mkdirs());
    }

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent event) {
        CoreUtil.loadFileManagers(
                configFile = new CoreConfigFile(path.getParent(), this),
                playerRepositoryFile = new PlayerRepositoryFile(path.getParent()),
                fileUpdateRepositoryFile = new FileUpdateRepositoryFile(path.getParent())
        );

        loadCorePlugins();

        Sponge.getCommandManager().register(this, new Command(createCommandLanguage()), "language");
        Sponge.getCommandManager().register(this, new Command(createCommandTranslation()), "translation");
        Sponge.getCommandManager().register(this, new Command(createCommandUpdate()), "update");

        new TranslationsUpdate(false).check();
        new PluginsUpdate(false).check();
        downloadTranslations();

        corePlugins.forEach(io.github.djxy.core.CorePlugin::loadTranslations);

        startUpdateInterval();
        initMetrics();
    }

    public int getIntervalUpdate() {
        return intervalUpdate;
    }

    public void setIntervalUpdate(int intervalUpdate) {
        this.intervalUpdate = intervalUpdate;
        startUpdateInterval();
    }

    @Listener
    public void onGameStoppedServerEvent(GameStoppedServerEvent event) {
        CoreUtil.saveFileManagers(configFile, playerRepositoryFile, fileUpdateRepositoryFile);
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Login event){
        PlayerRepository.getInstance().createPlayerIfNotExist(event.getProfile().getUniqueId());
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event){
        PlayerRepository.getInstance().setPlayerData(event.getTargetEntity().getUniqueId(), "name", event.getTargetEntity().getName());
        displayFileUpdates(event.getTargetEntity());
        displayPluginUpdates(event.getTargetEntity());
    }

    public void displayFileUpdates(Player player){
        FileUpdateRepository fur = FileUpdateRepository.getInstance();
        PlayerRepository pr = PlayerRepository.getInstance();

        if(fur.hasUpdate() && pr.getPlayerBoolean(player.getUniqueId(), PlayerRepository.RECEIVE_NOTIFICATION_FILES, true)){
            player.sendMessage(translator.translate(player, "fileUpdateHeader", null));
            for(CorePlugin corePlugin : corePlugins){
                Collection<FileUpdateRepository.FileUpdate> fileUpdates = fur.getFileUpdates(corePlugin.getName());

                if(!fileUpdates.isEmpty()) {
                    HashMap<String, Object> values = new HashMap<>();

                    values.put("plugin", corePlugin.getName());
                    values.put("nbFile", fileUpdates.size());
                    values.put("clickDownload", TextActions.executeCallback(e -> {
                        int fileToDownload = fileUpdates.size();

                        for (FileUpdateRepository.FileUpdate fileUpdate : fileUpdates) {
                            if (fileUpdate.canDownload()) {
                                fileToDownload--;
                                fileUpdate.download();
                            }
                        }

                        if (fileToDownload != fileUpdates.size()) {
                            player.sendMessage(translator.translate(player, "fileUpdateDownloadFinished", values));
                            corePlugin.loadTranslations();
                        } else
                            player.sendMessage(translator.translate(player, "fileUpdateDownloadNoFile", values));
                    }));

                    player.sendMessage(translator.translate(player, "fileUpdateRow", values));
                }
            }
        }
    }

    public void displayPluginUpdates(Player player){
        PluginUpdateRepository pur = PluginUpdateRepository.getInstance();
        PlayerRepository pr = PlayerRepository.getInstance();

        if(pur.hasUpdate() && pr.getPlayerBoolean(player.getUniqueId(), PlayerRepository.RECEIVE_NOTIFICATION_PLUGINS, true)){
            Collection<PluginUpdateRepository.PluginUpdate> pluginUpdates = pur.getPluginUpdates();
            player.sendMessage(translator.translate(player, "pluginUpdateHeader", null));

            for(PluginUpdateRepository.PluginUpdate pluginUpdate : pluginUpdates) {
                HashMap<String, Object> values = new HashMap<>();

                values.put("plugin", pluginUpdate.getName());
                values.put("version", pluginUpdate.getVersion());
                values.put("clickHere", TextActions.openUrl(pluginUpdate.getUrl()));

                player.sendMessage(translator.translate(player, "pluginUpdateRow", values));
            }
        }
    }

    public Node createCommandUpdate(){
        return new ChoiceNode("")
                .addNode(new ChoiceNode("plugins")
                        .setExecutor(new PluginUpdateExecutor())
                        .addNode(new ChoiceNode("set")
                                .addNode(new BooleanNode("receiveNotification", "value")
                                        .setExecutor(new PlayerSetReceiveNotification.Plugins()))))
                .addNode(new ChoiceNode("files")
                        .setExecutor(new FileUpdateExecutor())
                        .addNode(new ChoiceNode("set")
                                .addNode(new BooleanNode("receiveNotification", "value")
                                        .setExecutor(new PlayerSetReceiveNotification.Files()))));
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

    private void downloadTranslations(){
        for(CorePlugin corePlugin : corePlugins) {
            if (corePlugin.getTranslationPath() != null) {
                File path = corePlugin.getTranslationPath().toFile();

                if(path.listFiles().length == 0)
                    for (FileUpdateRepository.FileUpdate fileUpdate : FileUpdateRepository.getInstance().getFileUpdates(corePlugin.getName()))
                        fileUpdate.download();
            }
        }
    }

    private void startUpdateInterval(){
        if(updateTask != null)
            updateTask.cancel();

        updateTask = Sponge
                .getScheduler()
                .createTaskBuilder()
                .interval(intervalUpdate, TimeUnit.HOURS)
                .execute(e -> {
                    new TranslationsUpdate(true).check();
                    new PluginsUpdate(true).check();
                })
                .submit(this);
    }

    private void loadCorePlugins(){
        for(PluginContainer pluginContainer : Sponge.getPluginManager().getPlugins()){
            Optional<?> instance = pluginContainer.getInstance();

            if(instance.isPresent() && instance.get() instanceof CorePlugin)
                corePlugins.add((CorePlugin) instance.get());
        }
    }

    private void initMetrics(){
        try {
            for(CorePlugin plugin : corePlugins)
                new Metrics(Sponge.getGame(), plugin).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
