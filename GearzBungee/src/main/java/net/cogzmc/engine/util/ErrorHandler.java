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

package net.cogzmc.engine.util;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import net.cogzmc.engine.gearz.GearzBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Calendar;

/**
 * Error handler. Stores data about when there is an {@link java.lang.Exception} or other error that needs
 * to be stored in the Database for future reference. Useful for looking into errors that were missed
 * in the console, or narrowing down a specific cause of an error.
 */
public class ErrorHandler {
    /**
     * Reports an {@link java.lang.Exception} without bindings
     *
     * @param ex {@link java.lang.Exception} to report
     */
    public static void reportError(Exception ex) {
        storeErrorReport(BasicDBObjectBuilder.start("error", formatException(ex)).get());
        ex.printStackTrace();
    }

    /**
     * Reports an error for a {@link net.cogzmc.engine.server.Server} and {@link java.lang.Exception}
     *
     * @param serverInfo The {@link net.md_5.bungee.api.config.ServerInfo} with the error
     * @param ex         The {@link java.lang.Exception} involved in the error
     */
    @SuppressWarnings("unused")
    public static void reportError(ServerInfo serverInfo, Exception ex) {
        storeErrorReport(BasicDBObjectBuilder.start("server", serverInfo.getName()).add("error", formatException(ex)).get());
        ex.printStackTrace();
    }

    /**
     * Reports an error for a {@link net.md_5.bungee.api.connection.ProxiedPlayer}, their active {@link net.cogzmc.engine.server.Server}, and the {@link java.lang.Exception} related
     *
     * @param player The {@link net.md_5.bungee.api.connection.ProxiedPlayer} who is involved
     * @param ex     The {@link java.lang.Exception}
     */
    @SuppressWarnings("unused")
    public static void reportError(ProxiedPlayer player, Exception ex) {
        storeErrorReport(BasicDBObjectBuilder.start("player", player.getName()).add("server", player.getServer().getInfo().getName()).add("error", formatException(ex)).get());
        ex.printStackTrace();
    }

    /**
     * Reports an error without a key
     *
     * @param error Error to report
     */
    @SuppressWarnings("unused")
    public static void reportError(String error) {
        storeErrorReport(BasicDBObjectBuilder.start("error", error).get());
    }

    /**
     * Reports an error with a key.
     *
     * @param serverInfo {@link net.md_5.bungee.api.config.ServerInfo} error relevant to
     * @param error      The error itself.
     */
    @SuppressWarnings("unused")
    public static void reportError(ServerInfo serverInfo, String error) {
        storeErrorReport(BasicDBObjectBuilder.start("server", serverInfo.getName()).add("error", error).get());
    }

    /**
     * Reports an error with a key.
     *
     * @param key Easy reference to find error with a certain key
     * @param ex  The error itself.
     */
    @SuppressWarnings("unused")
    public static void reportError(String key, Exception ex) {
        storeErrorReport(BasicDBObjectBuilder.start("server", key).add("error", formatException(ex)).get());
    }

    /**
     * Reports an error
     *
     * @param player The {@link net.md_5.bungee.api.connection.ProxiedPlayer} to report the error on
     * @param error  The error string.
     */
    @SuppressWarnings("unused")
    public static void reportError(ProxiedPlayer player, String error) {
        storeErrorReport(BasicDBObjectBuilder.start("player", player.getName()).add("server", player.getServer().getInfo().getName()).add("error", error).get());
    }

    /**
     * Stores the {@link com.mongodb.DBObject}
     *
     * @param object The {@link com.mongodb.DBObject} to store
     */
    private static void storeErrorReport(DBObject object) {
        object.put("time", Calendar.getInstance().getTimeInMillis());
        DBCollection errors = GearzBungee.getInstance().getMongoDB().getCollection("errors");
        errors.save(object);
        GearzBungee.getInstance().getLogger().severe("Logging an error, check reports for more info.");
    }

    /**
     * Format {@link java.lang.Exception} for the database
     *
     * @param ex The {@link java.lang.Exception} to format.
     * @return List of database.
     */
    private static BasicDBList formatException(Exception ex) {
        BasicDBList stringList = new BasicDBList();
        for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
            stringList.add(String.format("%s(%s:%d)", stackTraceElement.getMethodName(), stackTraceElement.getFileName(), stackTraceElement.getLineNumber()));
        }
        return stringList;
    }

}
