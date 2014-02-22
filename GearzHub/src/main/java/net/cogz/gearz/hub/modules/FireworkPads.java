package net.cogz.gearz.hub.modules;

import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import net.tbnr.gearz.effects.FireworkUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@HubModuleMeta(
        key = "fireworkpads"
)
public class FireworkPads extends HubModule implements Listener {
    public FireworkPads() {
        super(false, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (event.getAction() != Action.PHYSICAL) return;
        if (!(event.getClickedBlock().getType().equals(Material.GOLD_PLATE) || event.getClickedBlock().getType().equals(Material.IRON_PLATE)))
            return;
        if (!event.getClickedBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WOOL)) return;
        FireworkUtils.getRandomFirework(event.getClickedBlock().getLocation().clone().add(0, 1, 0));
    }
}
