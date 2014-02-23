package net.cogz.gearz.hub.modules;

import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Colors signs.
 */
@HubModuleMeta(
        key = "coloredsigns"
)
public class ColoredSigns extends HubModule implements Listener {
    public ColoredSigns() {
        super(false, true);
    }

    /**
     * This is the event handler for a sign change event.
     */
    @EventHandler
    @SuppressWarnings("unused")
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("gearz.coloredsigns")) return;
        for (int x = 0, l = event.getLines().length; x < l; x++) {
            event.setLine(x, ChatColor.translateAlternateColorCodes('&', event.getLine(x))); //Color the f****** line
        }
    }
}
