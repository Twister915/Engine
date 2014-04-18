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

package net.cogz.chat;

import com.mongodb.*;
import lombok.Getter;
import net.cogz.chat.channels.ChannelCommand;
import net.cogz.chat.channels.ChannelManager;
import net.cogz.chat.channels.ChannelsListener;
import net.cogz.chat.data.Chat;
import net.cogz.chat.data.ChatManager;
import net.tbnr.gearz.config.GearzConfig;
import net.tbnr.util.TPlugin;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 4/14/2014
 */
@SuppressWarnings("FieldCanBeLocal")
public class GearzChat extends TPlugin {
    @Getter public static GearzChat instance;
    @Getter private Chat chat;
    @Getter private ChannelManager channelManager;
    @Getter GearzConfig channelConfig;
    @Override
    public void enable() {
        GearzChat.instance = this;
        this.chat = new Chat();
        this.channelManager = new ChannelManager();
        ChatManager chatManager = new ChatManager();
        registerCommands(new ChannelCommand());
        registerEvents(new ChannelsListener(channelManager));
        registerCommands(chatManager);
        registerEvents(chatManager);
        this.channelManager.registerChannels();
    }

    @Override
    public void disable() {}

    @Override
    public String getStorablePrefix() {
        return "gchat";
    }

    @Override
    public void initGearzConfigs() {
        this.channelConfig = new GearzConfig(this, "channels.yml");
        this.channelConfig.getConfig().options().copyDefaults(true);
        this.channelConfig.saveDefaultConfig();
    }

    //temporary methods to retrieve censored words
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

    protected void bungeeConfigSet(String key, Object value) {
        DBObject config = this.getBungeeConfig();
        config.put(key, value);
        this.getCollection().save(config);
    }

    private DBCollection getCollection() {
        return this.getMongoDB().getCollection("bungee_config");
    }

    public DBObject getBungeeConfig() {
        BasicDBObject object = new BasicDBObject("pl_name", "GearzBungee");
        DBCursor cursor = this.getCollection().find();
        DBObject obj = null;
        if (cursor.count() == 0) {
            obj = object;
        }
        return (obj == null) ? cursor.next() : obj;
    }
}
