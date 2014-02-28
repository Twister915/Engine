package net.cogz.gearz.hub.modules;

import net.cogz.gearz.hub.GearzHub;
import net.tbnr.gearz.effects.GearzLabelEntity;
import net.tbnr.util.TPlugin;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerDisconnectEvent;
import net.tbnr.util.player.TPlayerJoinEvent;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 8/30/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public final class MultiserverCannon implements ConfigurationSerializable, Listener {
    private final String server;
    private final Location referenceBlock;
    private final Location referenceLook;
    private final HashMap<TPlayer, GearzLabelEntity> labels;

    public MultiserverCannon(String server, String referenceBlock, String referenceLook) {
        this.server = server;
        this.referenceBlock = TPlugin.parseLocationString(referenceBlock);
        this.referenceLook = TPlugin.parseLocationString(referenceLook);
        if (GearzHub.getInstance().getArena() != null) {
            this.referenceBlock.setWorld(GearzHub.getInstance().getArena().getWorld());
            this.referenceLook.setWorld(GearzHub.getInstance().getArena().getWorld());
        }
        this.labels = new HashMap<>();
        for (TPlayer player : TPlayerManager.getInstance().getPlayers()) {
            label(player);
        }
    }

    public void connecting(TPlayer player) {
        this.labels.get(player).updateTag(GearzHub.getInstance().getFormat("formats.connecting", false, new String[]{"<server>", server}));
    }

    @EventHandler()
    @SuppressWarnings("unused")
    public void onPlayerJoin(final TPlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(GearzHub.getInstance(), new Runnable() {
            @Override
            public void run() {
                label(event.getPlayer());
            }
        }, 5);
    }

    private void label(TPlayer player) {
        this.labels.put(player, new GearzLabelEntity(player.getPlayer(), GearzHub.getInstance().getFormat("formats.server-label", false, new String[]{"<server>", this.server}), this.getReferenceBlock().clone().add(0, -0.4, 1)));

    }

    public void removeLabelAll() {
        for (Map.Entry<TPlayer, GearzLabelEntity> tPlayerGearzLabelEntityEntry : this.labels.entrySet()) {
            tPlayerGearzLabelEntityEntry.getValue().destroy();
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void disconnect(TPlayerDisconnectEvent event) {
        //labels.get(event.getPlayer()).destroy();
        labels.remove(event.getPlayer());
    }

    public String getServer() {
        return server;
    }

    public Location getReferenceBlock() {
        return referenceBlock;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> stuff = new HashMap<>();
        stuff.put("server", this.server);
        stuff.put("referenceBlock", GearzHub.encodeLocationString(this.referenceBlock));
        stuff.put("referenceLook", GearzHub.encodeLocationString(this.referenceLook));
        return stuff;
    }

    @SuppressWarnings("UnusedDeclaration")
    public MultiserverCannon(Map<String, Object> map) {
        this((String) map.get("server"), (String) map.get("referenceBlock"), (String) map.get("referenceLook"));
    }

    public Location getReferenceLook() {
        return this.referenceLook;
    }
}
