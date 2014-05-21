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

package net.cogzmc.engine.gearz;

import lombok.Getter;
import lombok.Setter;
import net.cogzmc.engine.activerecord.GModel;
import net.cogzmc.engine.gearz.config.GearzConfig;
import net.cogzmc.engine.gearz.effects.EnchantmentEffect;
import net.cogzmc.engine.gearz.effects.EnderBar;
import net.cogzmc.engine.gearz.game.single.GameManagerSingleGame;
import net.cogzmc.engine.gearz.netcommand.NetCommand;
import net.cogzmc.engine.gearz.network.GearzNetworkManagerPlugin;
import net.cogzmc.engine.gearz.network.GearzPlayerProvider;
import net.cogzmc.engine.gearz.player.GearzNickname;
import net.cogzmc.engine.server.Server;
import net.cogzmc.engine.server.ServerManager;
import net.cogzmc.engine.server.ServerManagerHelper;
import net.cogzmc.engine.util.*;
import net.cogzmc.engine.util.command.TCommandStatus;
import net.cogzmc.engine.util.delegates.PermissionsDelegate;
import net.cogzmc.engine.util.inventory.SelectorManager;
import net.cogzmc.engine.util.player.TPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gearz Plugin
 */
public final class Gearz extends TPlugin implements TDatabaseMaster, ServerManagerHelper {
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
    @Getter
    SelectorManager selectorManager;

    @Getter @Setter private GearzNetworkManagerPlugin networkManager;
    @Getter private boolean debug;

    public GearzPlayerProvider getPlayerProvider() {
        return this.networkManager.getPlayerProvider();
    }

    public static Random getRandom() {
        return random;
    }

    @Getter @Setter private PermissionsDelegate permissionsDelegate;

    @Getter
    GearzConfig databaseConfig;

    public Gearz() {
        Gearz.random = new Random();
    }

    @Override
    public void enable() {
        Gearz.instance = this;
        //** ENABLE **

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
        Bukkit.getScheduler().runTaskLater(Gearz.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Gearz.getInstance().isGameServer()) {
                    Server thisServer = ServerManager.getThisServer();
                    try {
                        thisServer.setAddress(IPUtils.getExternalIP());
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

    public static void handleCommandStatus(TCommandStatus status, CommandSender sender) {
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
        sender.sendMessage(Gearz.getInstance().getFormat("formats.command-status", true, new String[]{"<status>", Gearz.getInstance().getFormat(msgFormat, true)}));
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

    @Override
    @SuppressWarnings("unused")
    public boolean isGameServer() {
        return this.gamePlugins.size() > 0;
    }

    @Override
    public String getGame() {
        return (this.isGameServer() ? this.gamePlugins.get(0).getGameManager().getGameMeta().key() : null);
    }

    @Override
    public String getBungeeName() {
        return Gearz.bungeeName2;
    }

    public void debug(String string) {
        if (!isDebug()) return;
        getLogger().info(string);
    }
}
