package io.github.djxy.core.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-04-03.
 */
public class PlayerRepository {

    public static final String RECEIVE_NOTIFICATION_FILES = "receiveNotificationFilesUpdates";
    public static final String RECEIVE_NOTIFICATION_PLUGINS = "receiveNotificationPluginsUpdates";
    public static final String LANGUAGE = "language";

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

    public List<String> getPlayerKeys(UUID uuid){
        return new ArrayList<>(players.get(uuid).keySet());
    }

    public Boolean getPlayerBoolean(UUID uuid, String key){
        Object o = getPlayerData(uuid, key);

        return o instanceof Boolean? (Boolean) o :null;
    }

    public Boolean getPlayerBoolean(UUID uuid, String key, Boolean def){
        Object o = getPlayerData(uuid, key);

        return o instanceof Boolean? (Boolean) o :def;
    }

    public Double getPlayerDouble(UUID uuid, String key){
        Object o = getPlayerData(uuid, key);

        return o instanceof Double? (Double) o :null;
    }

    public Double getPlayerDouble(UUID uuid, String key, Double def){
        Object o = getPlayerData(uuid, key);

        return o instanceof Double? (Double) o :def;
    }

    public Integer getPlayerInteger(UUID uuid, String key){
        Object o = getPlayerData(uuid, key);

        return o instanceof Integer? (Integer) o :null;
    }

    public Integer getPlayerInteger(UUID uuid, String key, Integer def){
        Object o = getPlayerData(uuid, key);

        return o instanceof Integer? (Integer) o :def;
    }

    public String getPlayerString(UUID uuid, String key){
        Object o = getPlayerData(uuid, key);

        return o instanceof String? (String) o :null;
    }

    public String getPlayerString(UUID uuid, String key, String def){
        Object o = getPlayerData(uuid, key);

        return o instanceof String? (String) o :def;
    }

    public Object getPlayerData(UUID uuid, String key){
        return players.containsKey(uuid)?players.get(uuid).get(key):null;
    }

    public Object getPlayerData(UUID uuid, String key, Object def){
        return players.containsKey(uuid)?players.get(uuid).get(key):def;
    }

    public void setPlayerData(UUID uuid, String key, Object value){
        players.get(uuid).put(key, value);

        if(key.equals("name"))
            playersByName.put((String) value, uuid);
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
