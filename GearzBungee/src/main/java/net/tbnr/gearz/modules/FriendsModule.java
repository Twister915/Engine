package net.tbnr.gearz.modules;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Listener;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/23/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
public class FriendsModule implements TCommandHandler, Listener {
    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
