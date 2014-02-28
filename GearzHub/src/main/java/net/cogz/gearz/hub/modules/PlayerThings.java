package net.cogz.gearz.hub.modules;

import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import net.tbnr.util.player.TPlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 8/31/13
 * Time: 7:27 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
@HubModuleMeta(
        key = "texturepack"
)
public class PlayerThings extends HubModule implements Listener {

    private final String rescPackLink;

    public PlayerThings() {
        super(false, true);
        rescPackLink = getProperty("url");
    }

    @EventHandler
    public void onJoin(TPlayerJoinEvent event) {
        Player player = event.getPlayer().getPlayer();
        if (player.hasPermission("gearz.flight")) {
            player.setAllowFlight(true);
        }
        player.setResourcePack(rescPackLink);
    }

    @EventHandler
    public void inventoryMove(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) event.setCancelled(true);
    }
}
