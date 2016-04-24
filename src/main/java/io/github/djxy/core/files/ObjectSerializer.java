package io.github.djxy.core.files;

import ninja.leaping.configurate.ConfigurationNode;

/**
 * Created by Samuel on 2016-04-03.
 */
public interface ObjectSerializer {

    public void initFromNode(ConfigurationNode node);

    public void setNode(ConfigurationNode node);
}
