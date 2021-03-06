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

import com.mongodb.BasicDBList;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.tbnr.gearz.activerecord.GModel;
import net.tbnr.gearz.chat.ChatSpy;
import net.tbnr.gearz.chat.messaging.Messaging;
import net.tbnr.gearz.command.BaseReceiver;
import net.tbnr.gearz.command.NetCommandDispatch;
import net.tbnr.gearz.modules.*;
import net.tbnr.gearz.player.bungee.GearzPlayerManager;
import net.tbnr.util.TDatabaseManagerBungee;
import net.tbnr.util.TPluginBungee;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandStatus;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"NullArgumentToVariableArgMethod", "FieldCanBeLocal", "UnusedDeclaration"})
public final class GearzBungee extends TPluginBungee implements TDatabaseManagerBungee {
    /**
     * Gearz Instance
     */
    @Getter
    private static GearzBungee instance;
    /**
     * Responder object, in it's own thread
     */
    /**
     * The JEDIS pool object.
     */
    @Getter
    private JedisPool pool;
    /**
     * Random number generator
     */
    @Getter private static final Random random = new Random();
    /**
     * Stores the player manager.
     */
    private GearzPlayerManager playerManager;

    /**
     * Has our NetCommandDispatch for registration.
     */
    @Getter
    private NetCommandDispatch dispatch;

    @Getter
    private HelpMe helpMeModule;

    @Getter
    private ShuffleModule shuffleModule;

    @Getter
    private ListModule listModule;

    @Getter
    private HubModule hubModule;

    @Getter
    @Setter
    private boolean whitelisted = false;

    @Getter
    private SimpleDateFormat readable = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

    @Override
    protected void start() {
        //Load config
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        //Set instance
        GearzBungee.instance = this;

        //Setup redis and database
        GModel.setDefaultDatabase(this.getMongoDB());
        this.pool = new JedisPool(new JedisPoolConfig(), getConfig().getString("database.host"));
        this.dispatch = new NetCommandDispatch();
        this.getDispatch().registerNetCommands(new BaseReceiver());

        //New player manager
        this.playerManager = new GearzPlayerManager();

        //MOTD Handler
        MotdHandler motdHandler = new MotdHandler();

        //Online player manager
        this.listModule = new ListModule();

        //Hub server manager
        this.hubModule = new HubModule();

        //Helpme manager
        this.helpMeModule = new HelpMe();
        this.helpMeModule.registerReminderTask(30);

        //Player info module
        PlayerInfoModule infoModule = new PlayerInfoModule();

        //Game shuffle module
        this.shuffleModule = new ShuffleModule();

        //Bungee whitelist module
        WhitelistModule whitelistModule = new WhitelistModule();

        //Bungee announcer module
        AnnouncerModule announcerModule = new AnnouncerModule(getConfig().getBoolean("announcer.enabled", false));

        ChatSpy spy = new ChatSpy();

        TCommandHandler[] commandHandlers = {
                motdHandler,
                new Messaging(),
                this.hubModule,
                new UtilCommands(),
                new ServerModule(),
                new PlayerHistoryModule(),
                this.listModule,
                this.helpMeModule,
                infoModule,
                this.shuffleModule,
                whitelistModule,
                announcerModule,
                spy
        };

        Listener[] listeners = {
                this.playerManager,
                motdHandler,
                this.hubModule,
                this.listModule,
                this.helpMeModule,
                infoModule,
                this.shuffleModule,
                whitelistModule,
                spy
        };

        for (TCommandHandler handler : commandHandlers) {
            registerCommandHandler(handler);
        }

        for (Listener listener : listeners) {
            registerEvents(listener);
        }

        registerTabCompleter("gwhitelist", whitelistModule);

        ProxyServer.getInstance().getScheduler().schedule(this, new ServerModule.BungeeServerReloadTask(), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void stop() {
        saveConfig();
    }

    public Object[] getMotds() {
        Object motd = getBungeeConfig().get("motds");
        if (motd == null || !(motd instanceof BasicDBList)) {
            BasicDBList dbList = new BasicDBList();
            dbList.add("Another Gearz Server");
            getBungeeConfig().put("motds", dbList);
            return dbList.toArray();
        }
        return ((BasicDBList) motd).toArray();
    }

    public Object[] getAnnouncements() {
        Object announcements = getBungeeConfig().get("announcements");
        if (announcements == null || !(announcements instanceof BasicDBList)) {
            BasicDBList dbList = new BasicDBList();
            dbList.add("Another Gearz Server - Test Announcement");
            getBungeeConfig().put("announcements", dbList);
            return dbList.toArray();
        }
        return ((BasicDBList) announcements).toArray();
    }

    public Integer getInterval() {
        Object interval = getBungeeConfig().get("interval");
        if (interval == null || !(interval instanceof Integer)) {
            getBungeeConfig().put("interval", 60);
            return 60;
        }
        return (Integer) interval;
    }

    public int getMaxPlayers() {
        Object maxPlayers = bungeeConfigGet("max-players");
        if (maxPlayers == null || !(maxPlayers instanceof Integer)) return 1;
        return (Integer) maxPlayers;
    }

    @SuppressWarnings("unused")
    public void setMaxPlayers(Integer maxPlayers) {
        bungeeConfigSet("max-players", maxPlayers);
    }

    public void setMotds(BasicDBList motds) {
        bungeeConfigSet("motds", motds);
    }

    public void setAnnouncements(BasicDBList announcements) {
        bungeeConfigSet("announcements", announcements);
    }

    public void setInterval(Integer interval) {
        bungeeConfigSet("interval", interval);
    }

    @Override
    public String database() {
        return getConfig().getString("database.db");
    }

    @Override
    public String host() {
        return getConfig().getString("database.host");
    }

    @Override
    public int port() {
        return getConfig().getInt("database.port");
    }

    @SuppressWarnings("unused")
    public GearzPlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public Jedis getJedisClient() {
        return this.pool.getResource();
    }

    public void returnJedisClient(Jedis client) {
        this.pool.returnResource(client);
    }

    public static void handleCommandStatus(TCommandStatus status, CommandSender sender) {
        String msgFormat = null;
        switch (status) {
            case PERMISSIONS:
                msgFormat = "no-permission";
                break;
            case INVALID_ARGS:
                msgFormat = "bad-args";
                break;
            case FEW_ARGS:
                msgFormat = "few-args";
                break;
            case MANY_ARGS:
                msgFormat = "many-args";
                break;
            case WRONG_TARGET:
                msgFormat = "wrong-target";
                break;
        }
        if (msgFormat == null) return;
        sender.sendMessage(GearzBungee.getInstance().getFormat(msgFormat, true));
    }

    public List<String> getUserNames() {
        List<String> users = new ArrayList<>();
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            users.add(proxiedPlayer.getName());
        }
        return users;
    }
}
