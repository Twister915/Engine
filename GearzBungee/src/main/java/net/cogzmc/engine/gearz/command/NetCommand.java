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

package net.cogzmc.engine.gearz.command;

import lombok.Getter;
import net.cogzmc.engine.gearz.GearzBungee;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.HashMap;

/**
 * The {@link NetCommand} class is used solely to build commands to send.
 */
public class NetCommand {
    /**
     * The arguments of the command we're going to send.
     */
    @Getter
    private final HashMap<String, Object> args;
    /**
     * The name of the command.
     */
    @Getter
    private final String name;

    /**
     * Private constructor
     *
     * @param name The name.
     */
    private NetCommand(String name) {
        this.name = name;
        this.args = new HashMap<>();
    }

    /**
     * Create a command with the name.
     *
     * @param name The name of the command.
     * @return The NetCommand object. Ready for building.
     */
    public static NetCommand withName(String name) {
        return new NetCommand(name);
    }

    /**
     * Add an argument
     *
     * @param arg With name
     * @param o   and value
     * @return this object.
     */
    public NetCommand withArg(String arg, Object o) {
        this.args.put(arg, o);
        return this;
    }

    /**
     * Commits the command, and broadcasts it in the channel.
     */
    public void send() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", name);
            JSONObject argsObject = new JSONObject(args);
            jsonObject.put("data", argsObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Jedis jedis = GearzBungee.getInstance().getJedisClient();
        jedis.publish(NetDelegate.CHAN, jsonObject.toString());
        GearzBungee.getInstance().returnJedisClient(jedis);
    }
}
