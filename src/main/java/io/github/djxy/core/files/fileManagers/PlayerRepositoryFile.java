package io.github.djxy.core.files.fileManagers;

import io.github.djxy.core.files.FileManager;
import io.github.djxy.core.repositories.PlayerRepository;
import ninja.leaping.configurate.ConfigurationNode;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Samuel on 2016-04-13.
 */
public class PlayerRepositoryFile extends FileManager {

    public PlayerRepositoryFile(Path folder) {
        super(folder, "players");
    }

    @Override
    protected void save(ConfigurationNode root) {
        PlayerRepository pr = PlayerRepository.getInstance();

        for(UUID uuid : pr.getPlayersUUID()){
            for(String key : pr.getPlayerKeys(uuid)){
                root.getNode(uuid.toString(), key).setValue(pr.getPlayerData(uuid, key));
            }
        }
    }

    @Override
    protected void load(ConfigurationNode root) {
        PlayerRepository pr = PlayerRepository.getInstance();
        Map<Object, ConfigurationNode> players = (Map<Object, ConfigurationNode>) root.getChildrenMap();

        for(Object uuid : players.keySet()) {
            Map<Object, ConfigurationNode> keys = (Map<Object, ConfigurationNode>) players.get(uuid).getChildrenMap();
            UUID u = UUID.fromString((String) uuid);
            pr.createPlayerIfNotExist(u);

            for(Object key : keys.keySet())
                pr.setPlayerData(u, (String) key, keys.get(key).getValue());
        }
    }

}
