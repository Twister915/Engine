package net.tbnr.gearz.modules;

import net.md_5.bungee.api.CommandSender;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * Created by jake on 2/15/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class PropertiesManager implements TCommandHandler {


    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
