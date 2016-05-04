package io.github.djxy.core.commands.nodes.arguments;

import io.github.djxy.core.commands.nodes.ArgumentNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-04-08.
 */
public class WorldNode extends ArgumentNode {

    public WorldNode(String alias, String name) {
        super(alias, name);
    }

    public WorldNode(String alias) {
        super(alias);
    }

    @Override
    public Object getValue(String arg) {
        arg = arg.toLowerCase();

        for(World world : Sponge.getServer().getWorlds())
            if(world.getName().toLowerCase().equals(arg))
                return world;

        return null;
    }

    @Override
    public List<String> complete(String complete) {
        List<String> values = new ArrayList<>();

        for(World world : Sponge.getServer().getWorlds())
            if(world.getName().toLowerCase().startsWith(complete))
                values.add(world.getName());

        return values;
    }

}
