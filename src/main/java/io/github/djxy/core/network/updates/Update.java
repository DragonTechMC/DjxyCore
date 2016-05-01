package io.github.djxy.core.network.updates;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.CorePlugin;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.network.Github;
import io.github.djxy.core.repositories.PlayerRepository;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-04-29.
 */
public abstract class Update implements Runnable {

    private final String playerDataReceiveNotification;
    protected final boolean async;
    protected final Github github = new Github();
    protected final List<CorePlugin> plugins;
    protected List<Player> players;

    public Update(List<CorePlugin> plugins, String playerReceiveNotification) {
        this.plugins = plugins;
        this.playerDataReceiveNotification = playerReceiveNotification;
        this.async = true;
    }

    public Update(List<CorePlugin> plugins, String playerReceiveNotification, boolean async) {
        this.plugins = plugins;
        this.playerDataReceiveNotification = playerReceiveNotification;
        this.async = async;
    }

    public void check(){
        Optional<PermissionService> opt = Sponge.getServiceManager().provide(PermissionService.class);

        if(opt.isPresent()){
            PermissionService permissionService = opt.get();
            Set<Subject> subjects = permissionService.getUserSubjects().getAllWithPermission(Permissions.UPDATE).keySet();
            List<Player> players = new ArrayList<>();

            for(Subject subject : subjects){
                try{
                    UUID uuid = UUID.fromString(subject.getIdentifier());

                    if(PlayerRepository.getInstance().getPlayerBoolean(uuid, playerDataReceiveNotification, true))
                        players.add(Sponge.getServer().getPlayer(uuid).get());
                }catch (Exception e){}
            }

            this.players = players;

            if(async)
                new Thread(this).start();
            else
                run();
        }
    }

    protected void createSyncTask(Consumer task){
        Sponge.getScheduler().createTaskBuilder().execute(task).submit(CoreMain.getInstance());
    }

    protected URL createURL(String url){
        try {
            return new URL(url);
        } catch (MalformedURLException e) {}

        return null;
    }

}
