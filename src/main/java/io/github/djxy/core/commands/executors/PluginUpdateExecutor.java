package io.github.djxy.core.commands.executors;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.repositories.PluginUpdateRepository;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.action.TextActions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuel on 2016-05-03.
 */
public class PluginUpdateExecutor extends CommandExecutor {

    public PluginUpdateExecutor() {
        setPermission(Permissions.UPDATE);
    }

    @Override
    public void execute(CommandSource source, Map<String, Object> values) throws CommandException {
        PluginUpdateRepository pur = PluginUpdateRepository.getInstance();

        if(pur.hasUpdate()){
            Collection<PluginUpdateRepository.PluginUpdate> pluginUpdates = pur.getPluginUpdates();
            source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "pluginUpdateHeader", null));

            for(PluginUpdateRepository.PluginUpdate pluginUpdate : pluginUpdates) {
                HashMap<String, Object> map = new HashMap<>();

                map.put("plugin", pluginUpdate.getName());
                map.put("version", pluginUpdate.getVersion());
                map.put("clickHere", TextActions.openUrl(pluginUpdate.getUrl()));

                source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "pluginUpdateRow", map));
            }
        }
        else
            source.sendMessage(CoreMain.getTranslatorInstance().translate(source, "noPluginUpdate", null));
    }

}
