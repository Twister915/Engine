package net.tbnr.util.command;

import org.bukkit.command.CommandSender;

/**
 * Implement this to handle commands using the TCommand annotation.
 */
public interface TCommandHandler {
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType);
}
