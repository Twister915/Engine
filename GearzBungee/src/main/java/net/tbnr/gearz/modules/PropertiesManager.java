package net.tbnr.gearz.modules;

import net.md_5.bungee.api.CommandSender;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
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

    @TCommand(
            name = "properties",
            usage = "/properties <args..>",
            permission = "gearz.properties",
            aliases = {"props"},
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus properties(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 1) return TCommandStatus.INVALID_ARGS;
        GearzBungee instance = GearzBungee.getInstance();
        switch (args[0]) {
            case "reset":
                instance.resetStrings();
                sender.sendMessage(GearzBungee.getInstance().getFormat("properties-set", false));
                break;
            case "reload":
                instance.reloadStrings();
                sender.sendMessage(GearzBungee.getInstance().getFormat("properties-reload", false));
                break;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
