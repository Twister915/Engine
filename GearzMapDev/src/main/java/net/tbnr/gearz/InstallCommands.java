package net.tbnr.gearz;

import net.tbnr.util.FileUtil;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

/**
 * Created by Jake on 1/23/14.
 */
public class InstallCommands implements TCommandHandler {

    @TCommand(
            name = "install",
            usage = "/install <network> <minigame>",
            permission = "gearz.install",
            senders = {TCommandSender.Player, TCommandSender.Console})
         @SuppressWarnings("unused")
         public TCommandStatus install(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length != 2) {
            return TCommandStatus.INVALID_ARGS;
        }
        String network = args[0];
        String plugin = args[1];
        String url = GearzMapDev.getInstance().getConfig().getString("ci-url");
        url = url.replace("%plugin%", plugin).replace("%network%", network);
        if (FileUtil.downloadFile(url, GearzMapDev.getInstance().getServer().getWorldContainer() + "/plugins/")) {
            sender.sendMessage(GearzMapDev.getInstance().getFormat("installed", false, new String[]{"<plugin>", args[0]}));
        } else {
            sender.sendMessage(GearzMapDev.getInstance().getFormat("fail-installed", false, new String[]{"<plugin>", args[0]}));
        }

        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "remove",
            usage = "/remove <network> <minigame>",
            permission = "gearz.remove",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus remove(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length != 1) {
            return TCommandStatus.INVALID_ARGS;
        }
        String path = GearzMapDev.getInstance().getServer().getWorldContainer() + "/plugins/" + args[0] + ".jar";
        if (FileUtil.delete(new File(path))) {
            sender.sendMessage(GearzMapDev.getInstance().getFormat("un-installed", false, new String[]{"<plugin>", args[0]}));
        } else {
            sender.sendMessage(GearzMapDev.getInstance().getFormat("fail-uninstall", false, new String[]{"<plugin>", args[0]}));
        }

        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.getInstance().handleCommandStatus(status, sender, senderType);
    }
}
