package net.tbnr.util.command;

/**
 * All command sender types
 */
public enum TCommandSender {
    /**
     * The block command sender. This is when a Commandblock sends a command. Unsure on targets.
     */
    Block,
    /**
     * This is when a player sends a command.
     */
    Player,
    /**
     * This is when the console sends a command.
     */
    Console
}
