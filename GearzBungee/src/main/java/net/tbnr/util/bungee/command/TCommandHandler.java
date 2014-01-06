package net.tbnr.util.bungee.command;


import net.md_5.bungee.api.CommandSender;

/**
 * Implement this to handle commands using the TCommand annotation.
 */
public interface TCommandHandler {
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType);
}
