/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.engine.gearz.modules;

import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;
import com.mongodb.BasicDBList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.cogzmc.engine.gearz.GearzBungee;
import net.cogzmc.engine.util.bungee.command.TCommand;
import net.cogzmc.engine.util.bungee.command.TCommandHandler;
import net.cogzmc.engine.util.bungee.command.TCommandSender;
import net.cogzmc.engine.util.bungee.command.TCommandStatus;
import net.cogzmc.engine.util.io.FileUtil;
import net.cogzmc.engine.util.render.ImageToChatBungeeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.File;
import java.util.*;

/**
 * Handles the display of server MOTDs,
 * server images, and a fake {@link net.md_5.bungee.api.connection.ProxiedPlayer} list
 * to the {@link net.md_5.bungee.api.event.ProxyPingEvent}. This module also
 * displays the MOTD to the {@link net.md_5.bungee.api.connection.ProxiedPlayer} on join
 *
 * <p>
 * Latest Change: Add fake player list
 * <p>
 *
 * @author Joey
 * @since 9/28/13
 */
public class MotdHandler implements Listener, TCommandHandler {
    private List<String> motd;
    private ServerPing.PlayerInfo[] pingInfo;
    private Integer index;
    private String favicon;
    private final List<StaticMOTD> staticMotds = new LinkedList<>();
    private static final ChatColor[] motdPrefixColors = {ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.RED, ChatColor.YELLOW, ChatColor.GOLD};

    public MotdHandler() {
        index = 0;
        reload();
    }

    public void reload() {
        this.motd = GearzBungee.boxMessage(ChatColor.YELLOW, FileUtil.getData("motd.txt", GearzBungee.getInstance()));
        List<String> fakePlayerList = GearzBungee.boxMessage(ChatColor.YELLOW, FileUtil.getData("ping.txt", GearzBungee.getInstance()));
        List<ServerPing.PlayerInfo> listInfo = new ArrayList<>();
        for (String string : fakePlayerList) {
            listInfo.add(new ServerPing.PlayerInfo(string, UUID.randomUUID()));
        }
        if (listInfo.size() == 2){
            this.pingInfo = null;
        } else {
            this.pingInfo = listInfo.toArray(new ServerPing.PlayerInfo[listInfo.size()]);
        }
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
        String motd = null;
        boolean isStatic = false;
        if (this.staticMotds.size() > 0) {
            StaticMOTD staticMOTD = this.staticMotds.get(0);
            motd = staticMOTD.getMotd();
            if (staticMOTD.shouldRemove()) this.staticMotds.remove(0);
            isStatic = true;
        }
        if (motd == null) {
            Object[] motds = GearzBungee.getInstance().getMotds();
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
        }
        motd = GearzBungee.getInstance().getFormat("motd-format", false, true, new String[]{"<motd>", motd},
                new String[]{"<randomColor>", isStatic ? "" : motdPrefixColors[GearzBungee.getRandom().nextInt(motdPrefixColors.length)].toString()});
        if (pingInfo == null) {
            event.setResponse(new ServerPing(event.getResponse().getVersion(), new ServerPing.Players(GearzBungee.getInstance().getMaxPlayers(), ProxyServer.getInstance().getOnlineCount(), event.getResponse().getPlayers().getSample()), motd, this.favicon));
        } else {
            event.setResponse(new ServerPing(event.getResponse().getVersion(), new ServerPing.Players(GearzBungee.getInstance().getMaxPlayers(), ProxyServer.getInstance().getOnlineCount(), this.pingInfo), motd, this.favicon));
        }
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
            name = "motdreload",
            permission = "gearz.motd.reload",
            usage = "/motdreload",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus motdReload(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        reload();
        sender.sendMessage(ChatColor.GOLD + "MOTD data reloaded.");
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "staticmotd",
            permission = "gearz.setmotd",
            usage = "/staticmotd [minutes] [motd...]",
            senders = {TCommandSender.Player, TCommandSender.Console}
    )
    @SuppressWarnings("unused")
    public TCommandStatus staticMOTD(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length < 2) return TCommandStatus.FEW_ARGS;
        switch(args[0]) {
            case "add":
                Integer minutes;
                try {
                    minutes = Integer.valueOf(args[1]);
                } catch (NumberFormatException ex) {
                    return TCommandStatus.INVALID_ARGS;
                }
                String message = GearzBungee.getInstance().compile(args, 2, args.length);
                this.staticMotds.add(new StaticMOTD(message, minutes));
                sender.sendMessage(ChatColor.GREEN + "Added Static MOTD " + ChatColor.translateAlternateColorCodes('&', message));
                break;
            case "delete":
            case "remove":
                Integer id;
                try {
                    id = Integer.valueOf(args[1]);
                } catch (NumberFormatException ex) {
                    return TCommandStatus.INVALID_ARGS;
                }
                StaticMOTD removedMOTD = this.staticMotds.get(id);
                this.staticMotds.remove(removedMOTD);
                sender.sendMessage(ChatColor.GREEN + "Removed Static MOTD " + ChatColor.translateAlternateColorCodes('&', removedMOTD.getMotd()));
                break;
            case "list":
                sender.sendMessage(GearzBungee.getInstance().getFormat("header-motdlist", false));
                int index = 0;
                for (StaticMOTD motd : this.staticMotds) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("list-motdlist", false, true, new String[]{"<index>", String.valueOf(index)}, new String[]{"<motd>", motd.getMotd()}));
                    index++;
                }
                break;
            case "clear":
                this.staticMotds.clear();
                break;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
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
    @SuppressWarnings("unused")
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

    @Data
    @RequiredArgsConstructor
    private static class StaticMOTD {
        private final String motd;
        private final Date created = new Date();
        private final Integer lengthInMinutes;
        public boolean shouldRemove() {
            return (created.getTime() + (lengthInMinutes * 60000) <= (new Date()).getTime());
        }
    }
}