package io.github.djxy.core.commands.executors;

import io.github.djxy.core.CoreMain;
import io.github.djxy.core.Permissions;
import io.github.djxy.core.commands.CommandExecutor;
import io.github.djxy.core.repositories.PlayerRepository;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;

/**
 * Created by Samuel on 2016-04-30.
 */
public abstract class PlayerSetReceiveNotification extends CommandExecutor {

    private final String data;
    private final String translation;

    public PlayerSetReceiveNotification(String data, String translation) {
        setPermission(Permissions.UPDATE);
        this.data = data;
        this.translation = translation;
    }

    @Override
    public void execute(CommandSource source, Map<String, Object> values) throws CommandException {
        Boolean value = (Boolean) values.get("value");

        PlayerRepository.getInstance().setPlayerData(((Player) source).getUniqueId(), data, value);

        if(value)
            source.sendMessage(CoreMain.getTranslatorInstance().translate(source, translation+"True", values));
        else
            source.sendMessage(CoreMain.getTranslatorInstance().translate(source, translation+"False", values));
    }

    public static class Plugins extends PlayerSetReceiveNotification {

        public Plugins() {
            super(PlayerRepository.RECEIVE_NOTIFICATION_PLUGINS, "setReceiveNotificationPlugins");
        }
    }

    public static class Translations extends PlayerSetReceiveNotification {

        public Translations() {
            super(PlayerRepository.RECEIVE_NOTIFICATION_TRANSLATIONS, "setReceiveNotificationTranslations");
        }
    }

}
