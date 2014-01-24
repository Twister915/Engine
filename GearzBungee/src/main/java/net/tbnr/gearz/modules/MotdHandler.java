package net.tbnr.gearz.modules;

import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;
import com.mongodb.BasicDBList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.ImageToChatBungeeUtil;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/28/13
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("deprecation")
public class MotdHandler implements Listener, TCommandHandler {
    private List<String> motd;
    private Integer index;
    private String favicon;

    public MotdHandler() {
        index = 0;
        reload();
    }

    public void reload() {
        this.motd = GearzBungee.boxMessage(ChatColor.YELLOW, GearzBungee.getInstance().getData("motd.txt"));
        File fav = new File("server-icon.png");
        if (fav.exists()) {
            try {
                this.favicon = "data:image/png;base64," + BaseEncoding.base64().encode(Files.toByteArray(fav));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onMotdGrab(ProxyPingEvent event) {
        Object[] motds = GearzBungee.getInstance().getMotds();
        String motd = null;
        if (motds == null) motd = "Just another Gearz server";
        else {
            while (motd == null) {
                index++;
                if (index >= motds.length-1) {
                    index = 0;
                }
                Object motd1 = motds[index];
                if (!(motd1 instanceof String)) continue;
                motd = (String) motd1;
            }
        }
        motd = GearzBungee.getInstance().getFormat("motd-format", false, true, new String[]{"<motd>", motd}, new String[]{"<randomColor>", ChatColor.values()[GearzBungee.getRandom().nextInt(ChatColor.values().length)].toString()});
        /*event.setResponse(new ServerPing(
                ProxyServer.getInstance().getProtocolVersion(),
                ProxyServer.getInstance().getGameVersion(),
                motd,
                ProxyServer.getInstance().getOnlineCount(),
                GearzBungee.getInstance().getMaxPlayers())); - 1.6.4 */
        event.setResponse(new ServerPing(event.getResponse().getVersion(), new ServerPing.Players(GearzBungee.getInstance().getMaxPlayers(), ProxyServer.getInstance().getOnlineCount(), event.getResponse().getPlayers().getSample()), motd, this.favicon));
    }

    @TCommand(
            name = "motd",
            permission = "gearz.motd",
            usage = "/motd",
            senders = {TCommandSender.Player, TCommandSender.Console},
            aliases = {"messageoftheday"})
    @SuppressWarnings("unused")
    public TCommandStatus motdCommand(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        sendMotd(sender);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "setmotd",
            permission = "gearz.setmotd",
            usage = "/setmotd [list|remove|add] [message (required if applicable)]",
            senders = {TCommandSender.Player, TCommandSender.Console},
            aliases = {})
    @SuppressWarnings("unused")
    public TCommandStatus changeMotd(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length < 1) return TCommandStatus.HELP;
        String command = args[0];
        Object[] motds1 = GearzBungee.getInstance().getMotds();
        if (command.equalsIgnoreCase("list")) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("header-motdlist", false));
            int index = 0;
            for (Object o : motds1) {
                index++;
                if (!(o instanceof String)) continue;
                String s = (String) o;
                sender.sendMessage(GearzBungee.getInstance().getFormat("list-motdlist", false, true, new String[]{"<index>", String.valueOf(index)}, new String[]{"<motd>", s}));
            }
            return TCommandStatus.SUCCESSFUL;
        }
        if (args.length < 2) return TCommandStatus.FEW_ARGS;
        List<String> strings = new ArrayList<>();
        for (Object o : motds1) {
            if (o instanceof String) strings.add((String) o);
        }
        if (command.equalsIgnoreCase("remove")) {
            Integer toRemove = Integer.parseInt(args[1]);
            if (toRemove < 1 || toRemove > motds1.length) {
                sender.sendMessage(GearzBungee.getInstance().getFormat("index-out-of-range", false));
                return TCommandStatus.SUCCESSFUL;
            }
            String s = strings.get(toRemove - 1);
            strings.remove(toRemove - 1);
            sender.sendMessage(GearzBungee.getInstance().getFormat("removed-motd", false, true, new String[]{"<motd>", s}));
        } else if (command.equalsIgnoreCase("add")) {
            StringBuilder build = new StringBuilder();
            int index = 1;
            while (index < args.length) {
                build.append(args[index]).append(" ");
                index++;
            }
            build.substring(0, build.length() - 1);
            String s = build.toString();
            strings.add(s);
            sender.sendMessage(GearzBungee.getInstance().getFormat("added-motd", false, true, new String[]{"<motd>", s}));
        } else {
            return TCommandStatus.INVALID_ARGS;
        }
        BasicDBList basicDBList = new BasicDBList();
        basicDBList.addAll(strings);
        GearzBungee.getInstance().setMotds(basicDBList);
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PostLoginEvent event) {
        sendMotd(event.getPlayer());
    }

    private void sendMotd(final CommandSender player) {
        ProxyServer.getInstance().getScheduler().runAsync(GearzBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                List<String> headImage = ImageToChatBungeeUtil.getHeadImage(player.getName(), true);
                player.sendMessage(" ");
                for (int x = 0; x < Math.max(headImage.size(), motd.size()); x++) {
                    String headText = "";
                    String motdText = "";
                    if (x < headImage.size()) headText = headImage.get(x);
                    if (x < motd.size()) motdText = motd.get(x);
                    player.sendMessage(" " + headText + (headText.equals("") ? "" : " ") + ChatColor.RESET +
                            motdText.replaceAll("%player%", player.getName()).
                                    replaceAll("%online%", String.valueOf(ProxyServer.getInstance().getOnlineCount() + 1)));
                }
                player.sendMessage(" ");
            }
        });


    }
}