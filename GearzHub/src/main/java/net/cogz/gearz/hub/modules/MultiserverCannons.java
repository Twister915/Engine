package net.cogz.gearz.hub.modules;

import net.cogz.gearz.hub.annotations.HubModule;
import net.tbnr.gearz.Gearz;
import net.cogz.gearz.hub.GearzHub;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import net.tbnr.gearz.netcommand.BouncyUtils;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerWorldParticles;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.TPlugin;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import net.tbnr.util.player.TPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 8/30/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("ALL")
@HubModuleMeta(
        key = "servercannons"
)
public class MultiserverCannons extends HubModule implements Listener, TCommandHandler {
    private HashSet<TPlayer> actives = new HashSet<>();
    private HashMap<Location, MultiserverCannon> cannons = new HashMap<>();
    private MultiserverCannon cannon;

    public MultiserverCannons() {
        super(true, true);
        List pads = GearzHub.getInstance().getConfig().getList("pads");
        if (pads == null) return;
        for (Object cannon : pads) {
            if (!(cannon instanceof MultiserverCannon)) continue;
            this.cannons.put(((MultiserverCannon) cannon).getReferenceBlock(), (MultiserverCannon) cannon);
            GearzHub.getInstance().registerEvents((MultiserverCannon) cannon);
        }
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        final MultiserverCannon cannon = getCannon(event.getPlayer().getLocation().getBlock().getLocation());
        if (cannon == null) return;
        final TPlayer player = GearzHub.getInstance().getPlayerManager().getPlayer(event.getPlayer());
        final Player player1 = player.getPlayer();
        if (actives.contains(player)) return;
        event.setCancelled(true);
        cannon.connecting(player);
        player.playSound(Sound.FUSE);
        try {
            player.playParticleEffect(new TPlayer.TParticleEffect(player1.getLocation(), Gearz.getRandom().nextFloat(), 1, 35, 35, WrapperPlayServerWorldParticles.ParticleEffect.LARGE_SMOKE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //MultiserverCannonProcess multiserverCannonProcess = new MultiserverCannonProcess(player, cannon);
        //GearzHub.getInstance().registerEvents(multiserverCannonProcess);
        //Bukkit.getScheduler().scheduleSyncDelayedTask(GearzHub.getInstance(), multiserverCannonProcess, 35);
        this.actives.add(player);
        player1.setVelocity(player1.getLocation().getDirection().add(new Vector(0, 2, 0)));
        player1.setAllowFlight(true);
        player.playSound(Sound.ORB_PICKUP);
        Bukkit.getScheduler().runTaskLater(GearzHub.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!player1.isOnline()) return;
                actives.remove(player);
                player1.setAllowFlight(false);
                String serverFor = getServerFor(cannon.getServer(), false);
                if (serverFor == null) getServerFor(cannon.getServer(), true);
                if (serverFor == null) {
                    player1.sendMessage(GearzHub.getInstance().getFormat("formats.servers-full", false, new String[]{"<server>", cannon.getServer()}));
                    return;
                }
                BouncyUtils.sendPlayerToServer(player1, serverFor);
            }
        }, 40L);
        event.setCancelled(true);
    }

    private String getServerFor(String game, boolean allowFulls) {
        List<net.tbnr.gearz.server.Server> server = ServerManager.getServersWithGame(game);
        if (server == null) return game;
        Server server2 = null;
        for (Server s : server) {
            if (!s.getStatusString().equals("lobby")) continue;
            if (!s.isCanJoin()) continue;
            if (s.getMaximumPlayers() == s.getPlayerCount() && !allowFulls) continue;
            if (server2 == null) {
                server2 = s;
                continue;
            }
            if (s.getPlayerCount() > server2.getPlayerCount()) server2 = s;
        }
        if (server2 == null) return null;
        return server2.getBungee_name();
    }

    @TCommand(
            name = "setcannon",
            usage = "/setcannon <name>",
            permission = "gearz.setcannon",
            senders = {TCommandSender.Player})
    public TCommandStatus setCannon(org.bukkit.command.CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) return TCommandStatus.FEW_ARGS;
        if (args.length > 1) return TCommandStatus.MANY_ARGS;
        Player player = (Player) sender;
        Block pressure = player.getLocation().getBlock();
        Block coal = pressure.getRelative(BlockFace.DOWN);
        Block wool = coal.getRelative(BlockFace.DOWN);
        if (!(pressure.getType() == Material.STONE_PLATE && coal.getType() == Material.COAL_BLOCK && wool.getType() == Material.WOOL))
            return TCommandStatus.INVALID_ARGS;
        MultiserverCannon existing = getCannon(pressure.getLocation());
        MultiserverCannon cannon = new MultiserverCannon(args[0], TPlugin.encodeLocationString(pressure.getLocation()), TPlugin.encodeLocationString(player.getLocation()));
        List pads = GearzHub.getInstance().getConfig().getList("pads");
        if (pads == null) {
            pads = new ArrayList();
        }
        if (existing != null) {
            player.sendMessage(ChatColor.RED + "This pad already exists! Updating it :D");
            pads.remove(existing);
        }
        pads.add(cannon);
        cannons.put(cannon.getReferenceBlock(), cannon);
        GearzHub.getInstance().getConfig().set("pads", pads);
        GearzHub.getInstance().saveConfig();
        player.sendMessage(ChatColor.GREEN + "Setup a pad for " + args[0]);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "delcannon",
            usage = "/delcannon",
            permission = "gearz.delcannon",
            senders = {TCommandSender.Player})
    public TCommandStatus delCannon(org.bukkit.command.CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        Player player = (Player) sender;
        MultiserverCannon existing = getCannon(player.getLocation().getBlock().getLocation());
        if (existing == null) {
            return TCommandStatus.INVALID_ARGS;
        }
        List pads = GearzHub.getInstance().getConfig().getList("pads");
        if (!pads.contains(existing)) {
            return TCommandStatus.INVALID_ARGS;
        }
        pads.remove(existing);
        GearzHub.getInstance().getConfig().set("pads", pads);
        GearzHub.getInstance().saveConfig();
        existing.removeLabelAll();
        player.sendMessage(ChatColor.GREEN + "Removed a pad for " + existing.getServer());
        this.cannons.remove(existing.getReferenceBlock());
        return TCommandStatus.SUCCESSFUL;
    }

    private MultiserverCannon getCannon(Location location) {
        /*List pads = GearzHub.getInstance().getConfig().getList("pads");
        if (pads == null) {
            return null;
        }
        for (Object cannon : pads) {
            if (!(cannon instanceof MultiserverCannon)) continue;
            if (((MultiserverCannon)cannon).getReferenceBlock().equals(location)) return (MultiserverCannon)cannon;
        }
        return null;*/
        return this.cannons.containsKey(location) ? this.cannons.get(location) : null;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, org.bukkit.command.CommandSender sender, TCommandSender senderType) {
        GearzHub.handleCommandStatus(status, sender);
    }

    public static enum ProcessState {
        PRE_IGNITE,
        IGNITE,
        PROPEL,
        SEND,
        SENT
    }

    /*public static class MultiserverCannonProcess extends BukkitRunnable implements Listener {
        private TPlayer player;
        private MultiserverCannon cannon;
        private ProcessState state;
        private int propell_ticks = 0;
        private float pitch = 0;
        private float yaw = 0;

        public MultiserverCannonProcess(TPlayer player, MultiserverCannon cannon) {
            this.player = player;
            this.cannon = cannon;
            this.state = ProcessState.PRE_IGNITE;
        }

        @Override
        public void run() {
            try {
                if (cycle()) reregister(1);
            } catch (NullPointerException ex) {
                HandlerList.unregisterAll(this);
                return;
            }
        }

        private boolean cycle() {
            if (this.state == ProcessState.PRE_IGNITE) this.state = ProcessState.IGNITE;
            if (this.player == null) return false;
            if (!this.player.isOnline()) return false;
            switch (this.state) {
                case IGNITE:
                    this.player.playSound(Sound.EXPLODE);
                    try {
                        player.playParticleEffect(new TPlayer.TParticleEffect(player.getPlayer().getLocation(), Gearz.getRandom().nextFloat(), 1, 15, 10, WrapperPlayServerWorldParticles.ParticleEffect.EXPLODE));
                    } catch (Exception ignored) {
                    }
                    this.player.playSound(Sound.PORTAL_TRAVEL);
                    this.player.getPlayer().teleport(this.cannon.getReferenceLook());
                    this.player.getPlayer().setVelocity(this.player.getPlayer().getLocation().getDirection().multiply(1.9).add(new Vector(0, 0.5, 0)));
                    this.state = ProcessState.PROPEL;
                    this.pitch = this.player.getPlayer().getLocation().getPitch();
                    this.yaw = this.player.getPlayer().getLocation().getYaw();
                    return true;
                case PROPEL:
                    if (this.propell_ticks > 65) this.state = ProcessState.SEND;
                    this.propell_ticks++;
                    reregister(1);
                    /*try {
                        player.playParticleEffect(new TPlayer.TParticleEffect(player.getPlayer().getLocation(), new Location(player.getPlayer().getWorld(), 1, 1, 1), 25, 35, TPlayer.TParticleEffectType.LARGE_SMOKE));
                        for (TPlayer tPlayer : TPlayerManager.getInstance().getPlayers()) {
                            if (!tPlayer.getPlayer().canSee(player.getPlayer())) continue;
                            if (tPlayer.getPlayer().getLocation().distance(player.getPlayer().getLocation()) > 35) continue;
                            tPlayer.playParticleEffect(new TPlayer.TParticleEffect(player.getPlayer().getLocation(), new Location(player.getPlayer().getWorld(), 1, 1, 1), 15, 35, TPlayer.TParticleEffectType.SMOKE));
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.player.getPlayer().setVelocity(this.player.getPlayer().getLocation().getDirection().multiply(1.9).add(new Vector(0, 0.5, 0)));
                    return true;
                case SEND:
                    this.state = ProcessState.SENT;
                    String serverFor = getServerFor(cannon.getServer(), false);
                    if (serverFor == null) serverFor = getServerFor(cannon.getServer(), true);
                    if (serverFor == null) serverFor = cannon.getServer();
                    BouncyUtils.sendPlayerToServer(this.player.getPlayer(), serverFor);
                    this.player.getPlayer().teleport(GearzHub.getInstance().getSpawn().getSpawn());
                    try {
                        this.player.playParticleEffect(new TPlayer.TParticleEffect(player.getPlayer().getLocation(), Gearz.getRandom().nextFloat(), 1, 10, 2, WrapperPlayServerWorldParticles.ParticleEffect.HEART));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GearzHub.getInstance().getCannon().actives.remove(player);
                    return false;
            }
            return false;
        }

        private String getServerFor(String game, boolean allowFulls) {
            List<net.tbnr.gearz.server.Server> server = ServerManager.getServersWithGame(game);
            if (server == null) return game;
            Server server2 = null;
            for (Server s : server) {
                if (!s.getStatusString().equals("lobby")) continue;
                if (!s.isCanJoin()) continue;
                if (s.getMaximumPlayers() == s.getPlayerCount() && !allowFulls) continue;
                if (server2 == null) {
                    server2 = s;
                    continue;
                }
                if (s.getPlayerCount() > server2.getPlayerCount()) server2 = s;
            }
            if (server2 == null) return null;
            return server2.getBungee_name();
        }

        private void reregister(int time) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(GearzHub.getInstance(), this, time);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (this.state == ProcessState.SENT || this.state == ProcessState.PRE_IGNITE) return;
            if (!event.getPlayer().equals(this.player.getPlayer())) return;
            if (event.getTo().getPitch() == this.pitch && event.getTo().getYaw() == this.yaw) return;
            Location newTo = event.getTo();
            newTo.setPitch(this.pitch);
            newTo.setYaw(this.yaw);
            event.getPlayer().teleport(newTo);
        }
    }*/
}
