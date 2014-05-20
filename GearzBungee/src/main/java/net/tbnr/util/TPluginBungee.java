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

package net.tbnr.util;

import com.google.common.base.Preconditions;
import com.mongodb.*;
import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.tbnr.util.bungee.command.TCommandDispatch;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TTabCompleter;
import net.tbnr.util.bungee.cooldowns.TCooldownManager;
import net.tbnr.util.io.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/26/13
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TPluginBungee extends ConfigurablePlugin {
    private TCommandDispatch commandDispatch;
    private static DB mongoDB = null;
    private Properties properties;

    @Override
    public void onEnable() {
        if (this instanceof TDatabaseManagerBungee) getMongoDB();
        this.commandDispatch = new TCommandDispatch(this);
        if (!new File(getDataFolder() + File.separator + "strings.properties").exists()) saveStrings();
        this.properties = new Properties();
        reloadStrings();

        this.start();
    }

    @Override
    public void onDisable() {
        this.stop();
    }

    public DB getMongoDB() {
        if (TPluginBungee.mongoDB == null) {
            if (!(this instanceof TDatabaseManagerBungee)) return null;
            try {
                TPluginBungee.mongoDB = (new MongoClient(((TDatabaseManagerBungee) this).host(), ((TDatabaseManagerBungee) this).port())).getDB(((TDatabaseManagerBungee) this).database());
            } catch (UnknownHostException e) {
                ErrorHandler.reportError(e);
                return null;
            }
            TCooldownManager.database = TPluginBungee.mongoDB;
        }
        return TPluginBungee.mongoDB;
    }

    protected void bungeeConfigSet(String key, Object value) {
        DBObject config = this.getBungeeConfig();
        config.put(key, value);
        this.getCollection().save(config);
    }

    protected Object bungeeConfigGet(String key) {
        DBObject config = this.getBungeeConfig();
        if (!config.containsField(key)) return null;
        return config.get(key);
    }

    private DBCollection getCollection() {
        return this.getMongoDB().getCollection("bungee_config");
    }

    public DBObject getBungeeConfig() {
        BasicDBObject object = new BasicDBObject("pl_name", this.getDescription().getName());
        DBCursor cursor = this.getCollection().find();
        DBObject obj = null;
        if (cursor.count() == 0) {
            obj = object;
        }
        return (obj == null) ? cursor.next() : obj;
    }

    public void registerCommandHandler(TCommandHandler handler) {
        this.commandDispatch.registerHandler(handler);
    }

    public void registerTabCompleter(String command, TTabCompleter completer) {
        this.commandDispatch.registerTabCompleter(command, completer);
    }

    public void registerTabCompleter(TCommandHandler handler, TTabCompleter completer) {
        this.commandDispatch.registerTabCompleter(handler, completer);
    }

    public List<String> getDefaultTabComplete() {
        return this.commandDispatch.getDefaultTabComplete();
    }

    public void registerEvents(Listener listener) {
        getProxy().getPluginManager().registerListener(this, listener);
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

    protected abstract void start();

    protected abstract void stop();

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

    public void reloadStrings() {
        try {
            this.properties.load(new FileInputStream(new File(getDataFolder(), "strings.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveStrings() {
        Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin(this.getDescription().getName());
        Preconditions.checkNotNull(plugin);
        System.out.println(plugin.getClass().getSimpleName());
        File f = new File(getDataFolder().getAbsolutePath());
        if (!f.exists()) {
            if (!f.mkdirs()) return;
        }
        FileUtil.writeEmbeddedResourceToLocalFile("strings.properties", new File(getDataFolder(), "strings.properties"), plugin.getClass());
    }

    public String getFormat(String key, boolean prefix, boolean color, String[]... datas) {
        String property = ChatColor.translateAlternateColorCodes('&', this.properties.getProperty(prefix ? this.properties.getProperty("prefix") : "" + key, ""));
        if (datas == null) return property;
        for (String[] data : datas) {
            if (data.length != 2) continue;
            property = StringUtils.replace(property, data[0], data[1]);
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

}
