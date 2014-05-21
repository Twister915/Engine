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

import net.cogzmc.engine.gearz.GearzBungee;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.*;

/**
 * NetCommandDispatch is responsible for keeping track of associations between the registered netcommand handlers
 * and their respective netcommands.
 * It is also responsible for registration of commands, and handling commands, along with parsing the JSONObjects into
 * usable data for the handlers to act upon.
 */
public class NetCommandDispatch {
    /**
     * The delegate thread.
     */
    private final Thread delegateThread;
    /**
     * This field holds a cached value. Check inline comment
     */
    private final Map<String, NetCommandHandler> nameToAnnotationMap; //This is for caching the what would be for loop statement for finding what the command is based on a name.
    /**
     * This field holds the net commands to their respective annotations.
     */
    private final Map<NetCommandHandler, RegisteredNetCommand> netCommands;

    /**
     * This creates a new NetCommandDispatch. Nothing special about this, move along :).
     */
    public NetCommandDispatch() {
        this.netCommands = new HashMap<>();
        this.nameToAnnotationMap = new HashMap<>();
        NetDelegate netDelegate = new NetDelegate();
        this.delegateThread = new Thread(netDelegate);
        this.delegateThread.start();
    }

    /**
     * This will register an object to get calls when any annotated methods ask for them.
     *
     * @param o The object to register.
     */
    public void registerNetCommands(Object o) {
        for (Method m : o.getClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(NetCommandHandler.class)) continue;
            if (m.getParameterTypes().length != 1) continue;
            if (!m.getParameterTypes()[0].equals(HashMap.class)) continue;
            NetCommandHandler annotation = m.getAnnotation(NetCommandHandler.class);
            RegisteredNetCommand command;
            if (!this.netCommands.containsKey(annotation)) {
                command = new RegisteredNetCommand(annotation.name(), Arrays.asList(annotation.args()), new HashMap<Object, Method>());
                this.netCommands.put(annotation, command);
                this.nameToAnnotationMap.put(annotation.name(), annotation); //Other logic
            } else {
                command = this.netCommands.get(annotation);
            }
            command.registerHandler(o, m);
        }
    }

    /**
     * This will handle a command, and send calls to the respective NetCommand handlers.
     *
     * @param object The JSON Object that represents the command sent
     * @return If the NetCommand was handled.
     */
    public boolean handleCommand(JSONObject object) {
        String command;
        try {
            command = object.getString("command");
            NetCommandHandler netCommandHandler = this.nameToAnnotationMap.get(command);
            if (netCommandHandler == null) {
                GearzBungee.getInstance().getLogger().info("Unhandled command " + command + " " + object.toString());
                return false;
            }
            JSONObject data = object.getJSONObject("data");
            HashMap<String, Object> stringObjectHashMap = objectToHashMap(data);
            Set<String> strings = stringObjectHashMap.keySet();
            if (!strings.containsAll(Arrays.asList(netCommandHandler.args()))) return false;
            RegisteredNetCommand registeredNetCommand = this.netCommands.get(netCommandHandler);
            registeredNetCommand.callHandlers(stringObjectHashMap);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Converts a JSONObject into a Hash Map
     *
     * @param data The data object to convert
     * @return The HashMap<String, Object>
     * @throws org.json.JSONException
     */
    private HashMap<String, Object> objectToHashMap(JSONObject data) throws JSONException {
        HashMap<String, Object> returnVal = new HashMap<>();
        Iterator i = data.keys();
        while (i.hasNext()) {
            Object next = i.next();
            if (!(next instanceof String)) continue;
            String key = (String) next;
            Object o = data.get(key);
            returnVal.put(key, parseObject(o));
        }
        return returnVal;
    }

    /**
     * Converts an JSONArray into an ArrayList
     *
     * @param array the array to convert
     * @return the ArrayList object.
     * @throws org.json.JSONException
     */
    private ArrayList<Object> objectToArrayList(JSONArray array) throws JSONException {
        ArrayList<Object> objects = new ArrayList<>();
        int index = 0;
        while (index < array.length()) {
            objects.add(parseObject(array.get(index)));
            index++;
        }
        return objects;
    }

    /**
     * Runs the proper conversions on an object to make it readable.
     *
     * @param obj The object that you wish to convert.
     * @return The converted object.
     * @throws org.json.JSONException
     */
    private Object parseObject(Object obj) throws JSONException {
        Object o = obj;
        if (o instanceof JSONObject) {
            o = objectToHashMap((JSONObject) o);
        }
        if (o instanceof JSONArray) {
            o = objectToArrayList((JSONArray) o);
        }
        return o;
    }
}
