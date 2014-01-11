package net.tbnr.gearz;

import lombok.Getter;
import lombok.NonNull;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.tbnr.gearz.activerecord.GModel;
import net.tbnr.gearz.effects.EnchantmentEffect;
import net.tbnr.gearz.effects.EnderBar;
import net.tbnr.gearz.netcommand.NetCommand;
import net.tbnr.gearz.player.GearzNickname;
import net.tbnr.gearz.player.GearzPlayerUtils;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.gearz.server.ServerManagerHelper;
import net.tbnr.util.*;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Gearz Plugin
 */
public final class Gearz extends TPlugin implements TCommandHandler, TDatabaseMaster, ServerManagerHelper {
    /**
     * The Gearz instance.
     */
    private static Gearz instance;
    /**
     * Stores the Random for Gearz
     */
    private static Random random;

    private static String bungeeName2;
    /**
     * Jedis pool
     */
    private JedisPool jedisPool;

    public List<GearzPlugin> getGamePlugins() {
        return gamePlugins;
    }

    /**
     *
     */
    private List<GearzPlugin> gamePlugins;
    /**
     *
     */
    public static final String CHAN = "GEARZ_NETCOMMAND";


    /**
     * The chat.
     */
    @Getter
    private Chat chat;

    /**
     * The Permission
     */
    @Getter
    private Permission permission;

    public static Gearz getInstance() {
        return instance;
    }

    @Getter
    InventoryRefresher inventoryRefresher;

    public boolean showDebug() {
        return false;
    }

    public static Random getRandom() {
        return random;
    }

    public Gearz() {
        Gearz.instance = this;
        Gearz.random = new Random();
    }

    @Override
    public void enable() {
        EnderBar.resetPlayers();
        ServerManager.setHelper(this);
        if (Gearz.bungeeName2 == null) Gearz.bungeeName2 = RandomUtils.getRandomString(16);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Server is reloading!");
        }
        this.jedisPool = new JedisPool(getConfig().getString("redis.host"), getConfig().getInt("redis.port"));
        this.gamePlugins = new ArrayList<>();
        PluginManager pm = getServer().getPluginManager();
        if (pm.isPluginEnabled("zPermissions") && pm.isPluginEnabled("zChat")) {
            try {
                this.setupChat();
                this.setupPermission();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            GearzNickname nicknameHandler = new GearzNickname();
            registerEvents(nicknameHandler);
            registerCommands(nicknameHandler);
        }
        GearzPlayerUtils gearzPlayerUtils = new GearzPlayerUtils();
        registerEvents(gearzPlayerUtils);
        registerCommands(gearzPlayerUtils);
        registerEvents(new PlayerListener());
        registerEvents(new ColoredTablist());
        registerEvents(new EnderBar.EnderBarListeners());
        new TabListener();
        EnchantmentEffect.addEnchantmentListener();
        this.inventoryRefresher = new InventoryRefresher();
        GModel.setDefaultDatabase(this.getMongoDB());
        Gearz.getInstance().getConfig().set("bg.name", RandomUtils.getRandomString(16));
        Bukkit.getScheduler().runTaskLater(Gearz.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Gearz.getInstance().isGameServer()) {
                    Server thisServer = ServerManager.getThisServer();
                    try {
                        thisServer.setAddress(Gearz.getExternalIP());
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                    thisServer.setPort(Bukkit.getPort());
                    thisServer.save();
                }
                Gearz.getInstance().getLogger().info("Server linked and in the database");
            }
        }, 1);

    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider == null) {
            getLogger().info("Vault cannot be found, disabling!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        chat = chatProvider.getProvider();
    }

    private void setupPermission() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider == null) {
            getLogger().info("Vault cannot be found, disabling!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        permission = permissionProvider.getProvider();
    }

    @Override
    public void disable() {
        ServerManager.getThisServer().remove();
        NetCommand.withName("disconnect").withArg("name", Gearz.bungeeName2);
        EnderBar.resetPlayers();
    }

    @Override
    public String getStorablePrefix() {
        return "gearz";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, org.bukkit.command.CommandSender sender, TCommandSender senderType) {
        if (status == TCommandStatus.SUCCESSFUL) return;
        sender.sendMessage(getFormat("formats.command-status", true, new String[]{"<status>", status.toString()}));
    }

    @Override
    public TPlayerManager.AuthenticationDetails getAuthDetails() {
        return new TPlayerManager.AuthenticationDetails(getConfig().getString("database.host"), getConfig().getInt("database.port"), getConfig().getString("database.database"), getConfig().getString("database.collection"));
    }


    public Jedis getJedisClient() {
        return jedisPool.getResource();
    }

    public void returnJedis(Jedis jedis) {
        this.jedisPool.returnResource(jedis);
    }

    @SuppressWarnings("unused")
    public void registerGame(GearzPlugin plugin) {
        this.gamePlugins.add(plugin);
    }

    @SuppressWarnings("unused")
    public boolean isGameServer() {
        return this.gamePlugins.size() > 0;
    }

    private static void delete(@NonNull File file) {
        if (file.isDirectory()) {
            try {
                for (File f : file.listFiles()) {
                    delete(f);
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        if (!file.delete()) Gearz.getInstance().getLogger().warning("File: " + file + " could not be deleted");
    }

    @Override
    public String getGame() {
        return (this.isGameServer() ? this.gamePlugins.get(0).getGameManager().getGameMeta().key() : null);
    }

    public static String getBungeeName2() {
        return bungeeName2;
    }

    public String getBungeeName() {
        return Gearz.bungeeName2;
    }

    public static String getExternalIP() throws SocketException, IndexOutOfBoundsException {
        if (!Bukkit.getIp().equals("")) return Bukkit.getIp();
        NetworkInterface eth0 = NetworkInterface.getByName(Gearz.getInstance().getConfig().getString("network_interface"));

        if (eth0 == null) eth0 = NetworkInterface.getByName("eth0");

        Enumeration<InetAddress> inetAddresses = eth0.getInetAddresses();
        ArrayList<InetAddress> list = Collections.list(inetAddresses);
        InetAddress fin = null;
        for (InetAddress inetAddress : list) {
            if (inetAddress.getHostAddress().matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
                fin = inetAddress;
                break;
            }
        }
        return fin == null ? null : fin.getHostAddress();
    }
}
