package net.tbnr.util.bungee.command;

/**
 * The command statuses!
 */
public enum TCommandStatus {
    /**
     * Success!
     */
    SUCCESSFUL,
    /**
     * We don't have permission for this command!
     */
    PERMISSIONS,
    /**
     * Invalid arguments
     */
    INVALID_ARGS,
    /**
     * Too few arguments
     */
    FEW_ARGS,
    /**
     * Too many arguments
     */
    MANY_ARGS,
    /**
     * Return the help data
     */
    HELP,
    /**
     * The wrong target executed the command
     */
    WRONG_TARGET
}
