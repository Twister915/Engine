package net.cogz.gearz.hub.modules;

import net.cogz.gearz.hub.GearzHub;
import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
@HubModuleMeta(
        key = "restrictions"
)
public class Restrictions extends HubModule implements Listener {

    public Restrictions() {
        super(false, true);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getPlayer().hasPermission("gearz.hub.fall")) return;
        if (player.getLocation().getY() > 0) return;
        player.teleport(GearzHub.getInstance().getSpawnHandler().getSpawn());
        player.playSound(GearzHub.getInstance().getSpawnHandler().getSpawn(), Sound.CHICKEN_EGG_POP, 20, 1);
        player.sendMessage(GearzHub.getInstance().getFormat("tpd-spawn", true, new String[]{"<prefix>", GearzHub.getInstance().getChatPrefix()}));
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasPermission("gearz.hub.drop")) return;
        if (event.getItemDrop().getItemStack().getType() == Material.SNOW_BALL) return;
        event.getPlayer().sendMessage(GearzHub.getInstance().getFormat("cant-drop", true, new String[]{"<prefix>", GearzHub.getInstance().getChatPrefix()}));
        event.setCancelled(true);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) e.setCancelled(true);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onCraftItemEvent(CraftItemEvent e) {
        if (e.getWhoClicked().hasPermission("gearz.staff")) return;
        e.setCancelled(true);
    }
}
