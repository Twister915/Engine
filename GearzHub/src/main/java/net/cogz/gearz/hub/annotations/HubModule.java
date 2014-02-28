package net.cogz.gearz.hub.annotations;

import lombok.NonNull;
import net.cogz.gearz.hub.GearzHub;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

/**
 * Created by jake on 2/21/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class HubModule implements Listener, TCommandHandler {
    public HubModule(boolean commands, boolean listener) {
        if (commands) GearzHub.getInstance().registerCommands(this);
        if (listener) GearzHub.getInstance().registerEvents(this);
    }

    /**
     * Get property ~ Other object aka boolean etc.
     *
     * @param property ~ The property to get
     * @return Object ~ the property ~ Object
     */
    public final Object getPropertyObject(@NonNull String property) {
        HubModuleMeta name = getClass().getAnnotation(HubModuleMeta.class);
        if (name == null) return "";
        return GearzHub.getInstance().getConfig().get("hub-modules." + name.key() + ".properties." + property);
    }

    /**
     * Returns the configuration section
     *
     * @return Object ~ the configuration section
     */
    public final ConfigurationSection getConfigurationSection() {
        HubModuleMeta name = getClass().getAnnotation(HubModuleMeta.class);
        if (name == null) return null;
        return GearzHub.getInstance().getConfig().getConfigurationSection("hub-modules." + name.key() + ".properties");
    }

    /**
     * Get property like getFormat though it gets off property part
     * aka instead of getFormat("jaffa.othercategory.gsdjsdgdg")
     * it will automatically go to ("hub-items.<youritem>.properties.<property>")
     *
     * @param property the property to get
     * @return String ~ The property
     * @see net.tbnr.util.TPlugin#getFormat(String)
     */
    public final String getProperty(@NonNull String property) {
        return getProperty(property, false, new String[]{});
    }

    public final String getProperty(@NonNull String property, @NonNull boolean prefix) {
        return getProperty(property, prefix, new String[]{});
    }

    public final String getProperty(@NonNull String property, @NonNull boolean prefix, String[]... replacements) {
        HubModuleMeta name = getClass().getAnnotation(HubModuleMeta.class);
        if (name == null) return "";
        return GearzHub.getInstance().getFormat("hub-modules." + name.key() + ".properties." + property, prefix, replacements);
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzHub.handleCommandStatus(status, sender);
    }
}
