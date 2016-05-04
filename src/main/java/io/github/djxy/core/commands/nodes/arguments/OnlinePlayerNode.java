package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.commands.nodes.ArgumentNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-04-08.
 */
public class OnlinePlayerNode extends ArgumentNode {

    public OnlinePlayerNode(String alias, String name) {
        super(alias, name);
    }

    public OnlinePlayerNode(String alias) {
        super(alias);
    }

    @Override
    public Object getValue(String arg) {
        arg = arg.toLowerCase();

        for(Player player : Sponge.getServer().getOnlinePlayers())
            if(player.getName().toLowerCase().equals(arg))
                return player;

        return null;
    }

    @Override
    public List<String> complete(String complete) {
        List<String> values = new ArrayList<>();

        for(Player player : Sponge.getServer().getOnlinePlayers())
            if(player.getName().toLowerCase().startsWith(complete))
                values.add(player.getName());

        return values;
    }

}
