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

import com.mongodb.DB;
import net.tbnr.util.command.TCommandDispatch;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerJoinEvent;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The TPlugin class is used to represent a plugin! This will handle all the basics for you!
 */
public abstract class TPlugin extends JavaPlugin {
    private static final String divider = ";";
    /**
     * The command dispatch for the plugin.
     */
    private TCommandDispatch commandDispatch;
    /**
     * Player Manager
     */
    private static TPlayerManager playerManager = null;

    /**
     * This will register something for events simply
     *
     * @param listener The listener that you're registering
     */
    public final <T extends Listener> T registerEvents(T listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
        return listener;
    }

    /**
     * This registers a handler to handle commands!
     *
     * @param handler The handler to register.
     */
    public final void registerCommands(TCommandHandler handler) {
        this.getCommandDispatch().registerHandler(handler);
    }

    /**
     * The enable method, as specified by bukkit.
     */
    @Override
    public final void onEnable() {
        try {
            this.saveDefaultConfig(); //save the config
            this.initGearzConfigs();
            this.commandDispatch = new TCommandDispatch(this); //Create a new command dispatch
            if (TPlugin.playerManager == null && this instanceof TDatabaseMaster) {
                TPlugin.playerManager = new TPlayerManager(((TDatabaseMaster) this).getAuthDetails());
                this.registerEvents(this.getPlayerManager());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    TPlayer tPlayer = this.getPlayerManager().addPlayer(player);
                    Bukkit.getPluginManager().callEvent(new TPlayerJoinEvent(tPlayer));
                }
            }
            this.enable(); //Enable the plugin using the abstract method (hand this off to the plugin itself)
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /**
     * When the plugin is disabled, make sure to load the config, because most of the time when I reload the plugin
     * I mean to access the newest config.
     */
    @Override
    public final void onDisable() {
        this.reloadConfig();
        this.disable();
        this.commandDispatch = null;
    }

    /**
     * Plugins must implement this to be called on enable.
     */
    public abstract void enable();

    /**
     * Plugins must implement this to be called on disable.
     */
    public abstract void disable();

    public void initGearzConfigs() {}

    /**
     * Get command dispatch
     *
     * @return The command dispatch for this plugin
     */
    public TCommandDispatch getCommandDispatch() {
        return commandDispatch;
    }

    /**
     * Get the player manager
     *
     * @return The player manager for this TPlugin
     */
    public TPlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        return this.commandDispatch.onCommand(sender, command, s, strings);
    }

    /**
     * Parses a string intended to represent a location. Used for databases, or config files. Really cool
     *
     * @param string The location string to parse
     * @return The location that the string represents
     */
    public static Location parseLocationString(String string) {
        String[] sep = string.split(TPlugin.divider);
        if (sep.length < 6) {
            return null;
        }
        Location rv = new Location(Bukkit.getWorld(sep[0]), Double.valueOf(sep[1]), Double.valueOf(sep[2]), Double.valueOf(sep[3]));
        rv.setPitch(Float.parseFloat(sep[4]));
        rv.setYaw(Float.parseFloat(sep[5]));
        return rv;
    }

    /**
     * Encode a location into a string for storage
     *
     * @param location The location you intend to encode.
     * @return The string that represents the location.
     */
    public static String encodeLocationString(Location location) {
        return location.getWorld().getName() + TPlugin.divider + location.getX() + TPlugin.divider + location.getY() + TPlugin.divider + location.getZ() + TPlugin.divider + location.getPitch() + TPlugin.divider + location.getYaw();
    }


    /**
     * Get a String format from the config.
     *
     * @param formatPath Supplied configuration path.
     * @param color      Include colors in the passed args?
     * @param data       The data arrays. Used to insert variables into the config string. Associates Key to Value.
     * @return The formatted String
     */
    public final String getFormat(String formatPath, boolean color, String[]... data) {
        String string = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(formatPath, ""));
        if (data != null) {
            for (String[] dataPart : data) {
                if (dataPart.length < 2) continue;
                string = string.replaceAll(dataPart[0], dataPart[1]);
            }
        }
        if (color) {
            string = ChatColor.translateAlternateColorCodes('&', string);
        }
        return string;
    }

    public final String getFormat(String formatPath, String[]... data) {
        return getFormat(formatPath, true, data);
    }

    /**
     * Get the format without using any data.
     *
     * @param formatPath The path to the format!
     * @return The formatted message.
     */
    public final String getFormat(String formatPath) {
        return this.getFormat(formatPath, true);
    }

    /**
     * Get the format without using any data.
     *
     * @param formatPath The path to the format!
     * @param color      Include colors in the passed args?
     * @return The formatted message.
     */
    public final String getFormat(String formatPath, boolean color) {
        return this.getFormat(formatPath, color, new String[]{});
    }

    public abstract String getStorablePrefix();

    public final DB getMongoDB() {
        return this.getPlayerManager().getDatabase();
    }

    public void sendConsoleCommand(String command) {
        getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }

    public final <T extends Event> T callEvent(T event) {
        getServer().getPluginManager().callEvent(event);
        return event;
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

    public List<String> boxMessage(ChatColor firstColor, ChatColor secondColor, List<String> message) {
        List<String> stringList = new ArrayList<>();
        char[] chars = new char[50];
        Arrays.fill(chars, ' ');
        String result = new String(chars);
        stringList.add(firstColor + "" + ChatColor.STRIKETHROUGH + result);
        stringList.addAll(message);
        stringList.add(secondColor + "" + ChatColor.STRIKETHROUGH + result);
        return stringList;
    }

    public List<String> boxMessage(ChatColor firstColor, String... message) {
        return boxMessage(firstColor, firstColor, Arrays.asList(message));
    }

    public List<String> boxMessage(String... message) {
        return boxMessage(ChatColor.WHITE, message);
    }

    public List<String> boxMessage(ChatColor color, List<String> message) {
        return boxMessage(color, color, message);
    }

    public List<String> boxMessage(List<String> message) {
        return boxMessage(ChatColor.WHITE, message);
    }
}
