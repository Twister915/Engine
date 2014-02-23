package net.cogz.gearz.hub.modules;

import lombok.Getter;
import lombok.Setter;
import net.cogz.gearz.hub.GearzHub;
import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.netcommand.BouncyUtils;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List signsSection = GearzHub.getInstance().getConfig().getList("signs");
        if (signsSection == null) return;
        for (Object sign : signsSection) {
            if (!(sign instanceof ServerDisplaySign)) continue;
            this.signs.put(((ServerDisplaySign) sign).getLocation(), (ServerDisplaySign) sign);
        }
        GearzHub.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(GearzHub.getInstance(), new SignUpdateTask(), 0L, 1200L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (!(event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.WALL_SIGN)) return;
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
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasPermission("gearz.display.place")) return;
        if (!(event.getBlock().getType() == Material.SIGN || event.getBlock().getType() == Material.WALL_SIGN)) return;
        Sign sign = (Sign) event.getBlock().getState();
        if (sign.getLines().length != 2) return;
        String[] lines = sign.getLines();
        if (!lines[0].equals(GearzHub.getInstance().getFormat("sign-add-server", false))) return;
        String gameType = lines[1];
        ServerDisplaySign serverSign = new ServerDisplaySign(lines[1], GearzHub.encodeLocationString(sign.getLocation()));
        List signs = GearzHub.getInstance().getConfig().getList("signs");
        if (signs == null) {
            signs = new ArrayList();
        }
        signs.add(serverSign);
        this.signs.put(sign.getLocation(), serverSign);
        GearzHub.getInstance().getConfig().set("signs", signs);
        GearzHub.getInstance().saveConfig();
        event.getPlayer().sendMessage(ChatColor.GOLD + "Setup a sign for " + lines[1]);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getType() == Material.SIGN || event.getBlock().getType() == Material.WALL_SIGN)) return;
        if (!getSigns().containsKey(event.getBlock().getLocation())) return;
        if (!event.getPlayer().hasPermission("gearz.display.break")) return;
        ServerDisplaySign displaySign = getSigns().get(event.getBlock().getLocation());
        if (displaySign.getServer() == null) return;
        getSigns().remove(displaySign.getLocation());
    }

    public static class ServerDisplaySign implements ConfigurationSerializable {
        @Getter String gameType;
        @Getter Location location;
        @Getter @Setter Server server;

        public ServerDisplaySign(String gameType, String location) {
            this.gameType = gameType;
            this.location = GearzHub.parseLocationString(location);
        }

        private void update() {
            Block block = location.getBlock();
            if (block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN) return;
            Sign sign = (Sign) block.getState();
            List<Server> servers = ServerManager.getServersWithGame(gameType);
            if (servers.size() == 0) {
                sign.setLine(0, GearzHub.getInstance().getFormat("formats.sign-no-servers", false));
                return;
            }
            Server randomServer = servers.get(Gearz.getRandom().nextInt(servers.size()));
            setServer(randomServer);
            sign.setLine(0, GearzHub.getInstance().getFormat("formats.sign-line-0", false, new String[]{"<type>", this.gameType}));
            sign.setLine(1, GearzHub.getInstance().getFormat("formats.sign-line-1", false, new String[]{"<on>", randomServer.getPlayerCount() + ""}, new String[]{"<max>", randomServer.getMaximumPlayers() + ""}));
            sign.setLine(2, GearzHub.getInstance().getFormat("formats.sign-line-2", false, new String[]{"<status>", randomServer.getStatusString().toLowerCase()}));
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> toSerialize = new HashMap<>();
            toSerialize.put("gametype", gameType);
            toSerialize.put("location", GearzHub.encodeLocationString(this.location));
            return toSerialize;
        }
    }

    public class SignUpdateTask implements Runnable {
        @Override
        public void run() {
            for (ServerDisplaySign sign : ServerDisplaySigns.this.getSigns().values()) {
                sign.update();
            }
        }
    }
}
