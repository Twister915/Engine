package net.tbnr.util;

import com.mongodb.*;
import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;
import net.md_5.bungee.api.plugin.Listener;
import net.tbnr.util.bungee.command.TCommandDispatch;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.cooldowns.TCooldownManager;

import java.net.UnknownHostException;

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

    protected abstract void start();

    protected abstract void stop();
}
