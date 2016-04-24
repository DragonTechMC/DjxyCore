package io.github.djxy.core.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-04-03.
 */
public class PlayerRepository {

    private static final PlayerRepository instance = new PlayerRepository();

    public static PlayerRepository getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<UUID,ConcurrentHashMap<String,Object>> players = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,UUID> playersByName = new ConcurrentHashMap<>();

    private PlayerRepository() {
    }

    public void createPlayerIfNotExist(UUID uuid){
        if(!players.containsKey(uuid))
            players.put(uuid, new ConcurrentHashMap<>());
    }

    public void setPlayerName(UUID uuid, String name){
        players.get(uuid).put("name", name);
        playersByName.put(name, uuid);
    }

    public List<String> getPlayerKeys(UUID uuid){
        return new ArrayList<>(players.get(uuid).keySet());
    }

    public Object getPlayerData(UUID uuid, String key){
        return players.get(uuid).get(key);
    }

    public void setPlayerData(UUID uuid, String key, Object value){
        players.get(uuid).put(key, value);
    }

    public boolean hasPlayerData(UUID uuid, String key){
        return players.get(uuid).containsKey(key);
    }

    public UUID getPlayerUUID(String name){
        if(name != null)
            return playersByName.containsKey(name)?playersByName.get(name):null;

        return null;
    }

    public String getPlayerName(UUID uuid){
        return (String) players.get(uuid).get("name");
    }

    public List<String> getPlayersName() {
        return new ArrayList<>(playersByName.keySet());
    }

    public List<UUID> getPlayersUUID() {
        return new ArrayList<>(players.keySet());
    }

}
