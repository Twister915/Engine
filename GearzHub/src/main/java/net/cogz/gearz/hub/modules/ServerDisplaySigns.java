package net.cogz.gearz.hub.modules;

import lombok.Getter;
import net.cogz.gearz.hub.GearzHub;
import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import net.tbnr.gearz.netcommand.BouncyUtils;
import net.tbnr.gearz.server.Server;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jake on 2/22/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@HubModuleMeta(
        key = "displaysigns"
)
@SuppressWarnings("unused")
public class ServerDisplaySigns extends HubModule implements Listener {
    @Getter public HashMap<Location, ServerDisplaySign> signs = new HashMap<>();

    public ServerDisplaySigns() {
        super(false, true);
        List<String> signs = GearzHub.getInstance().getConfig().getStringList("signs");
        for (String sign : signs) {
            ServerDisplaySign serverDisplaySign = deSerialize(sign);
            getSigns().put(serverDisplaySign.getLocation(), serverDisplaySign);
            serverDisplaySign.update();
        }
        GearzHub.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(GearzHub.getInstance(), new SignUpdateTask(), 0L, 600L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (!(event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.WALL_SIGN))
            return;
        Sign sign = (Sign) event.getClickedBlock().getState();
        if (!getSigns().containsKey(sign.getLocation())) return;
        if (!event.getPlayer().hasPermission("gearz.display.use")) return;
        ServerDisplaySign displaySign = getSigns().get(sign.getLocation());
        if (displaySign.getServer() == null) return;
        Server server = displaySign.getServer();
        BouncyUtils.sendPlayerToServer(event.getPlayer(), server.getBungee_name());
    }

    @EventHandler
    @SuppressWarnings("ALL")
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("gearz.display.place")) return;
        String[] lines = event.getLines();
        if (!lines[0].equals(GearzHub.getInstance().getFormat("formats.sign-add-server", false))) return;
        String gameType = lines[1];
        ServerDisplaySign serverSign = new ServerDisplaySign(lines[1], GearzHub.encodeLocationString(event.getBlock().getLocation()));
        getSigns().put(serverSign.getLocation(), serverSign);
        serverSign.save();
        serverSign.update();
        event.getPlayer().sendMessage(ChatColor.GOLD + "Setup a sign for " + lines[1]);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getType() == Material.SIGN || event.getBlock().getType() == Material.WALL_SIGN)) return;
        if (!getSigns().containsKey(event.getBlock().getLocation())) return;
        if (!event.getPlayer().hasPermission("gearz.display.break")) return;
        ServerDisplaySign displaySign = getSigns().get(event.getBlock().getLocation());
        if (displaySign.getServer() == null) return;
        event.getPlayer().sendMessage(ChatColor.GOLD + "Broke a sign for " + displaySign.getGameType());
        displaySign.remove();
        getSigns().remove(displaySign.getLocation());
    }

    public ServerDisplaySign deSerialize(String string) {
        String[] serial = string.split(":");
        String type = serial[0];
        String locString = serial[1];
        return new ServerDisplaySign(type, locString);
    }

    public class SignUpdateTask implements Runnable {
        @Override
        public void run() {
            synchronized (ServerDisplaySigns.this.getSigns()) {
                for (ServerDisplaySign sign : ServerDisplaySigns.this.getSigns().values()) {
                    sign.update();
                }
            }
        }
    }
}
