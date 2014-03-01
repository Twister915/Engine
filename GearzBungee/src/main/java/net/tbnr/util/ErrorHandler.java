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

package net.tbnr.util;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;

import java.util.Calendar;

/**
 * Error handler. Stores data about when there's an exception or other error..
 */
public class ErrorHandler {
    /**
     * Reports an exception without bindings
     *
     * @param ex Exception to report
     */
    public static void reportError(Exception ex) {
        storeErrorReport(BasicDBObjectBuilder.start("error", formatException(ex)).get());
        ex.printStackTrace();
    }

    /**
     * Reports an error for a server and exception
     *
     * @param serverInfo The server with the error
     * @param ex         The exception involved in the error
     */
    @SuppressWarnings("unused")
    public static void reportError(ServerInfo serverInfo, Exception ex) {
        storeErrorReport(BasicDBObjectBuilder.start("server", serverInfo.getName()).add("error", formatException(ex)).get());
        ex.printStackTrace();
    }

    /**
     * Reports an error for a player, their active server, and the exception related
     *
     * @param player The player who is involved
     * @param ex     The exception
     */
    @SuppressWarnings("unused")
    public static void reportError(ProxiedPlayer player, Exception ex) {
        storeErrorReport(BasicDBObjectBuilder.start("player", player.getName()).add("server", player.getServer().getInfo().getName()).add("error", formatException(ex)).get());
        ex.printStackTrace();
    }

    /**
     * Reports an error without a binding
     *
     * @param error Error to report
     */
    @SuppressWarnings("unused")
    public static void reportError(String error) {
        storeErrorReport(BasicDBObjectBuilder.start("error", error).get());
    }

    /**
     * Reports an error with a binding.
     *
     * @param serverInfo Server error relavant to
     * @param error      The error itself.
     */
    @SuppressWarnings("unused")
    public static void reportError(ServerInfo serverInfo, String error) {
        storeErrorReport(BasicDBObjectBuilder.start("server", serverInfo.getName()).add("error", error).get());
    }

    /**
     * Reports an error
     *
     * @param player The player to report the error on
     * @param error  The error string.
     */
    @SuppressWarnings("unused")
    public static void reportError(ProxiedPlayer player, String error) {
        storeErrorReport(BasicDBObjectBuilder.start("player", player.getName()).add("server", player.getServer().getInfo().getName()).add("error", error).get());
    }

    /**
     * Stores the DB Object
     *
     * @param object The DBObject to store
     */
    private static void storeErrorReport(DBObject object) {
        object.put("time", Calendar.getInstance().getTimeInMillis());
        DBCollection errors = GearzBungee.getInstance().getMongoDB().getCollection("errors");
        errors.save(object);
        GearzBungee.getInstance().getLogger().severe("Logging an error, check reports for more info.");
    }

    /**
     * Format exception for the database
     *
     * @param ex The exception to format.
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
