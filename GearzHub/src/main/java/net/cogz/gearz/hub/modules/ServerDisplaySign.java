package net.cogz.gearz.hub.modules;

import lombok.Getter;
import net.cogz.gearz.hub.GearzHub;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jake on 2/23/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ServerDisplaySign {
    @Getter String gameType;
    @Getter Location location;
    @Getter Server server;

    public ServerDisplaySign(String gameType, String location) {
        this.gameType = gameType;
        this.location = GearzHub.parseLocationString(location);
    }

    public void update() {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign)) return;
        Sign sign = (Sign) block.getState();
        List<Server> servers = ServerManager.getServersWithGame(gameType);
        if (servers.size() == 0) {
            sign.setLine(0, GearzHub.getInstance().getFormat("formats.sign-no-servers", false));
            return;
        }
        Server randomServer = servers.get(Gearz.getRandom().nextInt(servers.size()));
        this.server = randomServer;
        sign.setLine(0, GearzHub.getInstance().getFormat("formats.sign-line-0", false, new String[]{"<type>", this.gameType}));
        sign.setLine(1, GearzHub.getInstance().getFormat("formats.sign-line-1", false, new String[]{"<on>", randomServer.getPlayerCount() + ""}, new String[]{"<max>", randomServer.getMaximumPlayers() + ""}));
        sign.setLine(2, GearzHub.getInstance().getFormat("formats.sign-line-2", false, new String[]{"<status>", randomServer.getStatusString().toLowerCase()}));
        sign.update(true);
    }

    public void save() {
        List<String> signs = GearzHub.getInstance().getConfig().getStringList("signs");
        if (signs == null) {
            signs = new ArrayList<>();
        }
        signs.add(serialize(this));
        GearzHub.getInstance().getConfig().set("signs", signs);
        GearzHub.getInstance().saveConfig();
    }

    public String serialize(ServerDisplaySign sign) {
        return sign.getGameType() + ":" + GearzHub.encodeLocationString(sign.getLocation());
    }

    public void remove() {
        List<String> signs = GearzHub.getInstance().getConfig().getStringList("signs");
        if (signs == null) return;
        String serial = serialize(this);
        List<String> toRemove = new ArrayList<>();
        for (String signString : signs) {
            if (signString.equals(serial)) {
                toRemove.add(signString);
            }
        }
        for (String remove : toRemove) {
            signs.remove(remove);
        }
        GearzHub.getInstance().getConfig().set("signs", signs);
        GearzHub.getInstance().saveConfig();
    }
}
