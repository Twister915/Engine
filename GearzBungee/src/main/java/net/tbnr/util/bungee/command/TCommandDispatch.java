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

package net.tbnr.util.bungee.command;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.tbnr.util.ErrorHandler;
import net.tbnr.util.TPluginBungee;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This is the dispatcher for all commands. It will handle commands and dispatch them to the proper methods.
 */
public final class TCommandDispatch {
    /**
     * Constructs the dispatcher
     *
     * @param plugin The plugin this dispatcher is for.
     */
    public TCommandDispatch(TPluginBungee plugin) {
        this.plugin = plugin;
    }

    /**
     * The local copy of the plugin
     */
    private final TPluginBungee plugin;
    /**
     * Associates commands to their respective handlers. One handler per command, obviously.
     */
    private final HashMap<String, TCommandHandler> handlers = new HashMap<>();
    /**
     * Associates the commands to their respective methods.
     */
    private final HashMap<String, Method> methods = new HashMap<>();
    /**
     * Associates commands with their metadata. This is easily done without this array, but if you get a variable, best store it.
     */
    private final HashMap<String, TCommand> metas = new HashMap<>();
    /**
     * Stores the delegates between Bungee and TCommand handler for all commands, since they each need to be their own class e_e
     */
    private final HashMap<String, BungeeCommandDelegate> delegates = new HashMap<>();

    private final HashMap<String, TTabCompleter> completers = new HashMap<>();
    /**
     * This is used as a utility to store the order of arguments, and their type for the executor method validation.
     */
    private static final Class[] argumentOrder = {CommandSender.class, TCommandSender.class, TCommand.class, String[].class};

    /**
     * Scans a class for valid commands, and places the data in the stores
     *
     * @param commandHandler This is the class to scan
     * @param plugin         A copy of the plugin, normally gotten from this object
     */
    private void scanClass(TCommandHandler commandHandler, TPluginBungee plugin) {
        Method[] methods1 = commandHandler.getClass().getDeclaredMethods();
        for (Method method : methods1) { //Loop through all methods for the class
            //This gets the annotation, otherwise known as the meta
            TCommand annotation = method.getAnnotation(TCommand.class);
            if (annotation == null) continue; //Although, if we don't have it, we don't need to check this method.
            if (method.getReturnType() != TCommandStatus.class) continue; //Checks for return type
            if (method.getParameterTypes().length != TCommandDispatch.argumentOrder.length)
                continue; //Checks arg length
            if (!Arrays.equals(method.getParameterTypes(), TCommandDispatch.argumentOrder)) continue; //Checks arg type
            if (this.delegates.containsKey(annotation.name())) {
                this.unregisterCommand(annotation.name());
            }
            BungeeCommandDelegate delegate = new BungeeCommandDelegate(annotation, this);
            ProxyServer.getInstance().getPluginManager().registerCommand(this.plugin, delegate);
            this.handlers.put(annotation.name(), commandHandler);
            this.methods.put(annotation.name(), method);
            this.metas.put(annotation.name(), annotation);
            this.delegates.put(annotation.name(), delegate);
        }
    }

    /**
     * Registers a handler
     *
     * @param handler The command handler to register
     */
    public void registerHandler(TCommandHandler handler) {
        this.scanClass(handler, this.plugin);
    }

    public void registerTabCompleter(String command, TTabCompleter completer) {
        this.completers.put(command, completer);
    }

    public void registerTabCompleter(TCommandHandler handler, TTabCompleter completer) {
        for (String command : getCommandsForHandler(handler)) {
            this.completers.put(command, completer);
        }
    }

    /**
     * Un-register a handler
     *
     * @param handler Un-registers a handler!
     */
    public void unregisterHandler(TCommandHandler handler) {
        for (String cmd : getCommandsForHandler(handler)) { //Removes associations for all found commands.
            this.unregisterCommand(cmd);
        }
    }

    private List<String> getCommandsForHandler(TCommandHandler handler) {
        ArrayList<String> commands = new ArrayList<>(); //This is where we will store all commands handled by the class
        for (String cmd : this.handlers.keySet()) { //Gets the commands handled by the class (populates "commands" (Line 83))
            TCommandHandler handler1 = this.handlers.get(cmd);
            if (handler1.equals(handler)) commands.add(cmd);
        }
        return commands;
    }

    /**
     * Unregister a command
     *
     * @param cmd The command to unregister.
     */
    public void unregisterCommand(String cmd) {
        methods.remove(cmd);
        handlers.remove(cmd);
        metas.remove(cmd);
        ProxyServer.getInstance().getPluginManager().unregisterCommand(delegates.get(cmd));
    }

    /**
     * Gets the associated handler to the command.
     *
     * @param command The command in question.
     * @return The handler associated with the command passed.
     */
    public TCommandHandler getHandler(String command) {
        return handlers.get(command);
    }

    /**
     * Gets the method invoked when the command is executed.
     *
     * @param command The command in question.
     * @return The method associated with the command passed.
     */
    public Method getMethod(String command) {
        return methods.get(command);
    }

    /**
     * Gets the metadata on the command
     *
     * @param command The command in question.
     * @return The meta associated with the command passed.
     */
    public TCommand getMeta(String command) {
        return metas.get(command);
    }

    public TTabCompleter getCompleter(String command) {
        return completers.get(command);
    }

    /**
     * Called by our Delegate
     *
     * @param sender The sender of the command
     * @param meta   The command's meta object.
     * @param args   The arguments passed.
     */
    public void onCommand(CommandSender sender, TCommand meta, String[] args) {
        try {
            String name = meta.name();
            TCommandHandler handler = getHandler(name);
            if (handler == null) {
                sender.sendMessage(ChatColor.RED + "There was no handler found for this command!");
                return;
            }
            TCommandSender typeSender = getType(sender);
            Method method = getMethod(name);
            boolean validType = false;
            for (TCommandSender tsender : meta.senders()) {
                if (tsender.equals(typeSender)) {
                    validType = true;
                    break;
                }
            }
            if (!validType) {
                handler.handleCommandStatus(TCommandStatus.WRONG_TARGET, sender, typeSender);
                return;
            }
            if (!meta.permission().equals("") && !sender.hasPermission(meta.permission())) {
                handler.handleCommandStatus(TCommandStatus.PERMISSIONS, sender, typeSender);
                return;
            }
            Object invoke = method.invoke(handler, sender, typeSender, meta, args);
            if (!(invoke instanceof TCommandStatus))
                throw new TCommandException("Invalid value returned for command status!");
            TCommandStatus status = (TCommandStatus) invoke;
            if (status == TCommandStatus.HELP) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/" + ChatColor.DARK_PURPLE + meta.name() + " " + ChatColor.AQUA + "- " + ChatColor.DARK_AQUA + meta.usage());
            }
            handler.handleCommandStatus(status, sender, typeSender);
        } catch (InvocationTargetException ex) {
            Throwable e = ex.getTargetException(); //Gets the actual exception. I think
            //Handles any thrown exceptions
            sender.sendMessage(ChatColor.DARK_RED + "An error occurred internally when executing this command. A detailed log is in the console!");
            sender.sendMessage(String.format("%s%s%s%s:%s%s", ChatColor.RED, ChatColor.ITALIC, e.getClass().getSimpleName(), ChatColor.DARK_AQUA, ChatColor.WHITE, e.getMessage()));
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            sender.sendMessage(ChatColor.RED + "at: " + ChatColor.GREEN + stackTraceElement.getClassName() + ChatColor.DARK_AQUA + ":" + ChatColor.GREEN + stackTraceElement.getLineNumber());
            ErrorHandler.reportError(ex);
        } catch (Exception ex) {
            ErrorHandler.reportError(ex);
        }
    }

    /**
     * Gets the type associated with the command sender.
     *
     * @param sender The actual sender object
     * @return The sender type (enum value)
     */
    private TCommandSender getType(CommandSender sender) {
        if (sender.getName().equals("CONSOLE")) return TCommandSender.Console;
        return TCommandSender.Player;
    }

    /**
     * BungeeCommandDelegate represents a "Command" that can serve as any command to send calls back to the dispatcher. :D
     */
    private class BungeeCommandDelegate extends Command implements TabExecutor {
        public BungeeCommandDelegate(TCommand command, TCommandDispatch dispatch) {
            super(command.name(), null, command.aliases());
            this.command = command;
            this.commandDispatch = dispatch;
        }

        private final TCommandDispatch commandDispatch;
        private final TCommand command;

        @Override
        public void execute(CommandSender commandSender, String[] strings) {
            try {
                this.commandDispatch.onCommand(commandSender, this.command, strings);
            } catch (Exception ex) {
                ErrorHandler.reportError(ex);
                commandSender.sendMessage(ChatColor.RED + "Very low level error. Cannot continue!");
            }
        }

        @Override
        public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
            TTabCompleter completer = this.commandDispatch.getCompleter(this.command.name());
            if (completer == null) return getDefaultTabComplete();
            List<String> list = completer.onTabComplete(sender, getType(sender), this, this.command, args);
            if (list == null) return Lists.newArrayList();
            else return getMatching(list, args[args.length - 1], true);
        }
    }

    public List<String> getDefaultTabComplete() {
        List<String> list = Lists.newArrayList();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            list.add(player.getName());
        }
        return list;
    }

    public List<String> getMatching(List<String> original, String query, boolean ignoreCase) {
        List<String> matching = new ArrayList<>();
        for (String ori : original) {
            if (ori.toLowerCase().startsWith(query.toLowerCase())) {
                matching.add(ori);
            }
        }
        return matching;
    }
}
