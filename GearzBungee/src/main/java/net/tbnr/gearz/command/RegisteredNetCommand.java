package net.tbnr.gearz.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Represents a registered NetCommand.
 */
@EqualsAndHashCode(of = {"name"})
public class RegisteredNetCommand {
    /**
     * The arguments that this command accepts
     */
    @Getter
    private final List<String> args;
    /**
     * Represents the name of the command
     */
    @Getter
    private final String name;
    /**
     * Associates handlers to their methods.
     */
    private final Map<Object, Method> handlers;

    /**
     * Creates a RegisteredNetCommand
     *
     * @param name     The name of the command
     * @param args     The accepted arguments
     * @param handlers Starting handlers.
     */
    public RegisteredNetCommand(String name, List<String> args, Map<Object, Method> handlers) {
        this.args = args;
        this.name = name;
        this.handlers = handlers;
    }

    /**
     * Register a handler to accept a method call.
     *
     * @param o The object which to execute the method on.
     * @param m The method.
     */
    public void registerHandler(Object o, Method m) {
        this.handlers.put(o, m);
    }

    /**
     * Calls all handlers for the method
     *
     * @param data Arguments of the NetCommand.
     */
    public void callHandlers(Map<String, Object> data) {
        for (Map.Entry<Object, Method> objectMethodEntry : this.handlers.entrySet()) {
            try {
                objectMethodEntry.getValue().invoke(objectMethodEntry.getKey(), data);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }
}
