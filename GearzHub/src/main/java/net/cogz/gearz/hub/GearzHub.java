package net.cogz.gearz.hub;

import com.mongodb.DBObject;
import lombok.Getter;
import net.cogz.gearz.hub.annotations.HubItems;
import net.cogz.gearz.hub.annotations.HubModules;
import net.cogz.gearz.hub.modules.MultiserverCannon;
import net.lingala.zip4j.exception.ZipException;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.GearzException;
import net.tbnr.gearz.arena.ArenaManager;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.TPlugin;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.SocketException;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 8/30/13
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("FieldCanBeLocal")
public class GearzHub extends TPlugin {
    @Getter private Spawn spawnHandler;
    private static GearzHub instance;
    @Getter
    private HubItems hubItems;
    @Getter
    private HubModules hubModules;
    @Getter private HubArena arena;

    public GearzHub() {
        ConfigurationSerialization.registerClass(MultiserverCannon.class);
    }

    public static GearzHub getInstance() {
        return instance;
    }

    @Override
    public void enable() {
	    GearzHub.instance = this;
	    Gearz.getInstance().setLobbyServer(true);
        DBObject hub_arena = getMongoDB().getCollection("hub_arena").findOne();
        if (hub_arena != null) {
            try {
                arena = (HubArena)ArenaManager.arenaFromDBObject(HubArena.class, hub_arena);
                arena.loadWorld();
            } catch (GearzException | ClassCastException | ZipException | IOException e) {
                e.printStackTrace();
            }
        }

	    spawnHandler = new Spawn();
	    hubItems = new HubItems("net.cogz.gearz.hub.items");
        hubModules = new HubModules("net.cogz.gearz.hub.modules");

        registerCommands(spawnHandler);
        registerEvents(hubItems);
	    new SaveAllTask().runTaskTimer(this, 0, 12000);

        ServerManager.setGame("lobby");
        ServerManager.setStatusString("HUB_DEFAULT");
        ServerManager.setOpenForJoining(true);
        Server thisServer = ServerManager.getThisServer();
        try {
            thisServer.setAddress(Gearz.getExternalIP());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        thisServer.setPort(Bukkit.getPort());
        thisServer.save();
    }

    @Override
    public void disable() {
    }

    @Override
    public String getStorablePrefix() {
        return "ghub";
    }

    public String getChatPrefix() {
        return getFormat("prefix");
    }

    public static void handleCommandStatus(TCommandStatus status, CommandSender sender) {
        if (status == TCommandStatus.SUCCESSFUL) return;
        sender.sendMessage(getInstance().getFormat("formats.command-status", true, new String[]{"<status>", status.toString()}));
    }

    public static class SaveAllTask extends BukkitRunnable {
        @Override
        public void run() {
            for (World world : Bukkit.getServer().getWorlds()) {
                world.setAutoSave(false);
                world.save();
                world.setAutoSave(true);
            }

            Bukkit.broadcast(ChatColor.GREEN + "World saved!", "gearz.notifysave");
            GearzHub.getInstance().getLogger().info("Saved the world.");
        }
    }

    public String compile(String[] args, int min, int max) {
        StringBuilder builder = new StringBuilder();
        for (int i = min; i < args.length; i++) {
            builder.append(args[i]);
            if (i == max) return builder.toString();
            builder.append(" ");
        }
        return builder.toString();
    }
}
