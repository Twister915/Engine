/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz;

import lombok.Getter;
import lombok.Setter;
import net.tbnr.gearz.activerecord.GModel;
import net.tbnr.gearz.config.GearzConfig;
import net.tbnr.gearz.effects.EnchantmentEffect;
import net.tbnr.gearz.effects.EnderBar;
import net.tbnr.gearz.game.single.GameManagerSingleGame;
import net.tbnr.gearz.netcommand.NetCommand;
import net.tbnr.gearz.network.GearzNetworkManagerPlugin;
import net.tbnr.gearz.network.GearzPlayerProvider;
import net.tbnr.gearz.player.GearzNickname;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.gearz.server.ServerManagerHelper;
import net.tbnr.gearz.settings.SettingsRegistration;
import net.tbnr.gearz.settings.commands.SettingsCommands;
import net.tbnr.util.*;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import net.tbnr.util.delegates.PermissionsDelegate;
import net.tbnr.util.inventory.SelectorManager;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
    @Getter private static Gearz instance;
    /**
     * Stores the Random for Gearz
     */
    private static Random random;
    private static String bungeeName2;
    /**
     * Jedis pool
     */
    private JedisPool jedisPool;
    @Getter private List<GearzPlugin> gamePlugins;
    public static final String CHAN = "GEARZ_NETCOMMAND";
    @Getter @Setter private boolean isLobbyServer;
    @Getter SelectorManager selectorManager;

    @Getter @Setter private GearzNetworkManagerPlugin networkManager;

    public GearzPlayerProvider getPlayerProvider() {
        return this.networkManager.getPlayerProvider();
    }

    public boolean showDebug() {
        return false;
    }

    public static Random getRandom() {
        return random;
    }

    @Getter @Setter private PermissionsDelegate permissionsDelegate;

    @Getter GearzConfig databaseConfig;

    public Gearz() {
        Gearz.random = new Random();
    }

    @Override
    public void enable() {
        Gearz.instance = this;

        //** ENABLE **
        //This method is a bit confusing. Let's comment/clean it up a bit <3

        //Reset all players for the EnderBar
        EnderBar.resetPlayers();

        //Setup the helper delegation methods
        ServerManager.setHelper(this);

        //Setup a new Bungee Name variable.
        if (Gearz.bungeeName2 == null) Gearz.bungeeName2 = RandomUtils.getRandomString(16);

        //Kick all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Server is reloading!");
        }

        //Setup the Jedis pool so we can communicate with BungeeCord using our NetCommand system.
        this.jedisPool = new JedisPool(getDatabaseConfig().getConfig().getString("redis.host"), getDatabaseConfig().getConfig().getInt("redis.port"));

        //Setup an array to hold a list of all Gearz plugins.
        this.gamePlugins = new ArrayList<>();

        GearzNickname nicknameHandler = new GearzNickname();
        registerEvents(nicknameHandler);
        registerCommands(nicknameHandler);

        //Generic player utils
        registerEvents(new PlayerListener());
        registerEvents(new EnderBar.EnderBarListeners());
        registerCommands(new SettingsCommands());
        new TabListener();

        //EnderBar utils
        registerEvents(new EnderBar.EnderBarListeners());

        //Silk Touch 32 listener for effects
        EnchantmentEffect.addEnchantmentListener();

        //Setup the inv refresher. This is used in the server selector.
        this.selectorManager = new SelectorManager();

        //Setup some hooks for the GModel class to connect to our database.
        GModel.setDefaultDatabase(this.getMongoDB());

        //Deprecated.
        Gearz.getInstance().getConfig().set("bg.name", RandomUtils.getRandomString(16));

        //Link the server up in the database.
        SettingsRegistration.register();
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
                    GameManagerSingleGame temp = (GameManagerSingleGame) getGamePlugins().get(0).getGameManager();
                    registerCommands(temp);
                }
                Gearz.getInstance().getLogger().info("Server linked and in the database");
            }
        }, 1);

	    registerEvents(new RedFactory());
    }

    @Override
    public void disable() {
        saveConfig();
        getDatabaseConfig().saveConfig();
        ServerManager.getThisServer().remove();
        NetCommand.withName("disconnect").withArg("name", Gearz.bungeeName2);
        EnderBar.resetPlayers();
    }

    @Override
    public void initGearzConfigs() {
        this.databaseConfig = new GearzConfig(this, "database.yml");
        this.databaseConfig.getConfig().options().copyDefaults(true);
        this.databaseConfig.saveDefaultConfig();
    }

    public void activatePermissionsFeatures() {
        registerEvents(new ColoredTablist());
    }

    @Override
    public String getStorablePrefix() {
        return "gearz";
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, org.bukkit.command.CommandSender sender, TCommandSender senderType) {
        if (status == TCommandStatus.SUCCESSFUL) return;
        String msgFormat = null;
        switch (status) {
            case PERMISSIONS:
                msgFormat = "formats.no-permission";
                break;
            case INVALID_ARGS:
                msgFormat = "formats.bad-args";
                break;
            case FEW_ARGS:
                msgFormat = "formats.few-args";
                break;
            case MANY_ARGS:
                msgFormat = "formats.many-args";
                break;
            case WRONG_TARGET:
                msgFormat = "formats.wrong-target";
                break;
        }
        if (msgFormat == null) return;
        sender.sendMessage(Gearz.getInstance().getFormat(msgFormat, true));
    }

    @Override
    public TPlayerManager.AuthenticationDetails getAuthDetails() {
        return new TPlayerManager.AuthenticationDetails(getDatabaseConfig().getConfig().getString("database.host"), getDatabaseConfig().getConfig().getInt("database.port"), getDatabaseConfig().getConfig().getString("database.database"), getDatabaseConfig().getConfig().getString("database.collection"));
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

    @Override
    public String getGame() {
        return (this.isGameServer() ? this.gamePlugins.get(0).getGameManager().getGameMeta().key() : null);
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

    public void debug(String string) {
        if (!showDebug()) return;
        getLogger().info(string);
    }
}
