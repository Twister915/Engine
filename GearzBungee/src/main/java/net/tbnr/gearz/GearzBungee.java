/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.activerecord.GModel;
import net.tbnr.gearz.chat.Chat;
import net.tbnr.gearz.chat.ChatManager;
import net.tbnr.gearz.chat.ClearChat;
import net.tbnr.gearz.chat.Messaging;
import net.tbnr.gearz.chat.channels.ChannelCommand;
import net.tbnr.gearz.chat.channels.ChannelManager;
import net.tbnr.gearz.chat.channels.ChannelsListener;
import net.tbnr.gearz.command.BaseReceiver;
import net.tbnr.gearz.command.NetCommandDispatch;
import net.tbnr.gearz.modules.*;
import net.tbnr.gearz.player.bungee.GearzPlayerManager;
import net.tbnr.gearz.player.bungee.PermissionsDelegate;
import net.tbnr.util.FileUtil;
import net.tbnr.util.TDatabaseManagerBungee;
import net.tbnr.util.TPluginBungee;
import net.tbnr.util.bungee.command.TCommandStatus;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * - Reconnect attempts
 * - Register on Site TODO
 * - Help command
 */
@SuppressWarnings({"NullArgumentToVariableArgMethod", "FieldCanBeLocal", "UnusedDeclaration"})
public class GearzBungee extends TPluginBungee implements TDatabaseManagerBungee {
    /**
     * Gearz Instance
     */
    private static GearzBungee instance;
    /**
     * Stores the static strings file loaded into memory
     */
    @Getter private Properties strings;
    /**
     * Responder object, in it's own thread
     */
    /**
     * The JEDIS pool object.
     */
    private JedisPool pool;
    /**
     * Random number generator
     */
    private static final Random random = new Random();
    /**
     * Stores the player manager.
     */
    private GearzPlayerManager playerManager;
    /**
     * Stores chat utils
     */
    private ChatManager chatUtils;

    /**
     * Stores chat data
     */
    @Getter
    private Chat chat;

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
    private Hub hub;

    @Getter
    @Setter
    private boolean whitelisted;

    @Getter
    private ChannelManager channelManager;

    @Getter
    public SimpleDateFormat readable;

    @Getter
    public ChatManager chatManager;

    @Setter @Getter
    private PermissionsDelegate permissionsDelegate;

    /**
     * Gets the current instance of the GearzBungee plugin.
     *
     * @return The instance.
     */
    public static GearzBungee getInstance() {
        return GearzBungee.instance;
    }

    public static Random getRandom() {
        return random;
    }

    @Override
    protected void start() {
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        GearzBungee.instance = this;
        readable = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        whitelisted = false;
        GModel.setDefaultDatabase(this.getMongoDB());
        this.pool = new JedisPool(new JedisPoolConfig(), getConfig().getString("database.host"));
        //this.responder = new ServerResponder();
        this.dispatch = new NetCommandDispatch();
        this.getDispatch().registerNetCommands(new BaseReceiver());
        this.playerManager = new GearzPlayerManager();
        registerEvents(this.playerManager);
        MotdHandler motdHandler = new MotdHandler();
        registerEvents(motdHandler);
        registerCommandHandler(motdHandler);
        this.chatUtils = new ChatManager();
        this.chat = new Chat();
        registerCommandHandler(new Messaging());
        registerEvents(this.chatUtils);
        registerCommandHandler(this.chatUtils);
        hub = new Hub();
        registerEvents(hub);
        registerCommandHandler(hub);
        registerCommandHandler(new UtilCommands());
        registerCommandHandler(new ServerModule());
        registerCommandHandler(new PlayerHistoryModule());
        listModule = new ListModule();
        registerCommandHandler(listModule);
        registerEvents(listModule);
        if (!new File(getDataFolder() + File.separator + "strings.properties").exists()) saveStrings();
        this.strings = new Properties();
        reloadStrings();
        this.helpMeModule = new HelpMe();
        this.helpMeModule.registerReminderTask(30);
        registerCommandHandler(this.helpMeModule);
        registerEvents(this.helpMeModule);
        PlayerInfoModule infoModule = new PlayerInfoModule();
        registerCommandHandler(infoModule);
        registerEvents(infoModule);
        this.shuffleModule = new ShuffleModule();
        registerEvents(this.shuffleModule);
        registerCommandHandler(this.shuffleModule);
        ReportModule.ReportManager reportManager = new ReportModule.ReportManager(getMongoDB().getCollection("reports"));
        ReportModule reportModule = new ReportModule(reportManager);
        registerCommandHandler(reportModule);
        WhitelistModule whitelistModule = new WhitelistModule();
        registerEvents(whitelistModule);
        registerCommandHandler(whitelistModule);
        AnnouncerModule announcerModule = new AnnouncerModule(getConfig().getBoolean("announcer.enabled", false));
        registerCommandHandler(announcerModule);
        registerCommandHandler(new StatsModule());
        registerCommandHandler(new PropertiesManager());
        channelManager = new ChannelManager();
        if (getConfig().getBoolean("channels.enabled", false)) {
            getLogger().info("Channels enabled...");
            registerEvents(new ChannelsListener());
            channelManager.registerChannels();
            registerCommandHandler(new ChannelCommand());
        } else {
            ModBroadcast modBroadcast = new ModBroadcast();
            registerEvents(modBroadcast);
            registerCommandHandler(modBroadcast);
            getLogger().info("Channels disabled...");
        }
        this.chatManager = new ChatManager();
        registerCommandHandler(new ClearChat());
        ProxyServer.getInstance().getScheduler().schedule(this, new ServerModule.BungeeServerReloadTask(), 0, 1, TimeUnit.SECONDS);
    }

    public void reloadStrings() {
        try {
            this.strings.load(new FileInputStream(getDataFolder() + File.separator + "strings.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveStrings() {
        FileUtil.writeEmbeddedResourceToLocalFile("strings.properties", new File(getDataFolder() + File.separator + "strings.properties"));
    }

    public void resetStrings() {
        saveStrings();
        reloadStrings();
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

    public String[] getCensoredWords() {
        Object censoredWords = getBungeeConfig().get("censoredWords");
        if (censoredWords == null || !(censoredWords instanceof BasicDBList)) {
            return new String[0];
        }
        BasicDBList dbListCensored = (BasicDBList) censoredWords;
        return dbListCensored.toArray(new String[dbListCensored.size()]);
    }

    public void setCensoredWords(BasicDBList dbList) {
        bungeeConfigSet("censoredWords", dbList);
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

    public String getFormat(String key, boolean prefix, boolean color, String[]... datas) {
        if (this.strings.getProperty(key) == null) {
            return key;
        }
        String property = this.strings.getProperty(key);
        if (prefix)
            property = ChatColor.translateAlternateColorCodes('&', this.strings.getProperty("prefix")) + property;
        property = ChatColor.translateAlternateColorCodes('&', property);
        if (datas == null) return property;
        for (String[] data : datas) {
            if (data.length != 2) continue;
            property = property.replaceAll(data[0], data[1]);
        }
        if (color) property = ChatColor.translateAlternateColorCodes('&', property);
        return property;
    }

    public String getFormat(String key, boolean prefix, boolean color) {
        return getFormat(key, prefix, color, null);
    }

    public String getFormat(String key, String[]... data) {
        return getFormat(key, false, false, data);
    }

    public String getFormat(String key, boolean prefix) {
        return getFormat(key, prefix, true);
    }

    public String getFormat(String key) {
        return getFormat(key, true);
    }

    public List<String> getData(String file) {
        File f = new File(getDataFolder(), file);
        if (!(f.canRead() && f.exists())) try {
            boolean newFile = f.createNewFile();
            if (!newFile) return null;
            getData(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        BufferedReader stream;
        try {
            stream = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(f))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        List<String> lines = new ArrayList<>();
        String line;
        try {
            while ((line = stream.readLine()) != null) {
                lines.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return lines;
    }

    public static List<String> boxMessage(ChatColor firstColor, ChatColor secondColor, List<String> message) {
        List<String> stringList = new ArrayList<>();
        char[] chars = new char[50];
        Arrays.fill(chars, ' ');
        String result = new String(chars);
        stringList.add(firstColor + "" + ChatColor.STRIKETHROUGH + result);
        stringList.addAll(message);
        stringList.add(secondColor + "" + ChatColor.STRIKETHROUGH + result);
        return stringList;
    }

    public static List<String> boxMessage(ChatColor firstColor, String... message) {
        return boxMessage(firstColor, firstColor, Arrays.asList(message));
    }

    @SuppressWarnings("unused")
    public static List<String> boxMessage(String... message) {
        return boxMessage(ChatColor.WHITE, message);
    }

    public static List<String> boxMessage(ChatColor color, List<String> message) {
        return boxMessage(color, color, message);
    }

    @SuppressWarnings("unused")
    public static List<String> boxMessage(List<String> message) {
        return boxMessage(ChatColor.WHITE, message);
    }

    public Jedis getJedisClient() {
        return this.pool.getResource();
    }

    public void returnJedisClient(Jedis client) {
        this.pool.returnResource(client);
    }

    /*
        @SuppressWarnings("unused")
        @Deprecated
        public ServerResponder getResponder() {
            return responder;
        }*/
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

    public static void connectPlayer(ProxiedPlayer player1, String server) {
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
        if (serverInfo == null) {
            player1.sendMessage(GearzBungee.getInstance().getFormat("server-not-online", true, true));
            return;
        }
        if (player1.getServer().getInfo().getName().equals(server)) {
            player1.sendMessage(GearzBungee.getInstance().getFormat("already-connected"));
            return;
        }
        player1.sendMessage(GearzBungee.getInstance().getFormat("connecting", true, true));
        player1.connect(serverInfo);
    }

    public List<String> getUserNames() {
        List<String> users = new ArrayList<>();
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            users.add(proxiedPlayer.getName());
        }
        return users;
    }
}
