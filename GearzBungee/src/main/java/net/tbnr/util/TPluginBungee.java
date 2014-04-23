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

import com.mongodb.*;
import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Listener;
import net.tbnr.util.bungee.command.TCommandDispatch;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.cooldowns.TCooldownManager;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    public void onEnable() {
        if (this instanceof TDatabaseManagerBungee) getMongoDB();
        this.commandDispatch = new TCommandDispatch(this);
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
}
