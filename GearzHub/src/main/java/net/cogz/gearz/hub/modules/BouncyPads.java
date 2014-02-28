package net.cogz.gearz.hub.modules;

import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 8/31/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
@HubModuleMeta(
        key = "bouncypads"
)
public class BouncyPads extends HubModule implements Listener {
    public BouncyPads() {
        super(false, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (event.getAction() != Action.PHYSICAL) return;
        if (!(event.getClickedBlock().getType().equals(Material.STONE_PLATE) || event.getClickedBlock().getType().equals(Material.WOOD_PLATE)))
            return;
        if (!event.getClickedBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WOOL)) return;
        event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(2.2).add(new Vector(0, 1.5, 0))); //What the fuck ever.
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BAT_TAKEOFF, 2, 0);
        event.setCancelled(true);
    }
}
