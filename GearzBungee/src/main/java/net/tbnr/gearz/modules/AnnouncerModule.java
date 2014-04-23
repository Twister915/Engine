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

package net.tbnr.gearz.modules;

import com.mongodb.BasicDBList;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Module to manage automaticaly broadcasted
 * announcents.
 *
 * <p>
 * Latest Change: Created module
 * <p>
 *
 * @author Jake
 * @since 10/28/2013
 */
@SuppressWarnings("unused")
public class AnnouncerModule implements Runnable, TCommandHandler {
    @Getter
    private List<Announcement> announcements;
    private boolean running;
    private ScheduledTask thisSchedule = null;
    private int interval_seconds = 0;
    private int current;

    public AnnouncerModule(List<Announcement> announcementList, int interval) {
        this.announcements = announcementList;
        this.interval_seconds = interval;
    }

    public AnnouncerModule(boolean start) {
        this.interval_seconds = GearzBungee.getInstance().getInterval();
        Object[] announceList = GearzBungee.getInstance().getAnnouncements();
        List<Announcement> finalAnnouncements = new ArrayList<>();
        for (Object a : announceList) {
            if (!(a instanceof String)) continue;
            // String ann = (String) a; never used
            finalAnnouncements.add(new Announcement((String) a));
        }
        this.announcements = finalAnnouncements;
        if (start) {
            start();
        }
    }

    @TCommand(aliases = {"announcer"}, usage = "/announcer", senders = {TCommandSender.Player, TCommandSender.Console}, permission = "gearz.announcer", name = "announcer")
    @SuppressWarnings("unused")
    public TCommandStatus announcer(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-help"));
            return TCommandStatus.SUCCESSFUL;
        }
        Object[] list = GearzBungee.getInstance().getAnnouncements();
        if (args[0].equalsIgnoreCase("list")) {
            for (int x = 0; x < announcements.size(); x++) {
                sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-list", false, false, new String[]{"<num>", x + ""}, new String[]{"<announcement>", announcements.get(x).getColoredText()}));
            }
            return TCommandStatus.SUCCESSFUL;
        }
        List<String> strings = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof String) strings.add((String) o);
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-badargs"));
                return TCommandStatus.INVALID_ARGS;
            }
            String s = GearzBungee.getInstance().compile(args, 1, args.length);
            strings.add(s);
            sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-add"));
        } else if (args[0].equalsIgnoreCase("remove")) {
            Integer toRemove = Integer.parseInt(args[1]);
            if (toRemove < 1 || toRemove > list.length) {
                sender.sendMessage(GearzBungee.getInstance().getFormat("index-out-of-range", false));
                return TCommandStatus.SUCCESSFUL;
            }
            //String s = strings.get(toRemove - 1); never used
            strings.remove(toRemove - 1);
            sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-remove", false, false, new String[]{"<num>", toRemove + ""}));
        } else if (args[0].equalsIgnoreCase("interval")) {
            if (args[1] != null) {
                int num;
                try {
                    num = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-notanum"));
                    return TCommandStatus.INVALID_ARGS;
                }
                GearzBungee.getInstance().setInterval(num);
                this.interval_seconds = num;
                sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-interval-set", false, false, new String[]{"<num>", num + ""}));
            }
        } else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-help"));
            return TCommandStatus.SUCCESSFUL;
        } else if (args[0].equalsIgnoreCase("restart") || args[0].equalsIgnoreCase("start")) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-restart"));
            reschedule();
            return TCommandStatus.SUCCESSFUL;
        } else if (args[0].equalsIgnoreCase("stop")) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("announcer-stop"));
            cancel();
            return TCommandStatus.SUCCESSFUL;
        }
        BasicDBList basicDBList = new BasicDBList();
        basicDBList.addAll(strings);
        List<Announcement> finalAnnouncements = new ArrayList<>();
        for (String string : strings) {
            finalAnnouncements.add(new Announcement(string));
        }
        this.announcements = finalAnnouncements;
        GearzBungee.getInstance().setAnnouncements(basicDBList);
        return TCommandStatus.SUCCESSFUL;
    }

    public String compile(String[] args, int min, int max) {
        return GearzBungee.getInstance().compile(args, min, max);
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    public static class Announcement {
        private final String rawText;

        public Announcement(String text) {
            this.rawText = text;
        }

        public String getColoredText() {
            return ChatColor.translateAlternateColorCodes('&', rawText);
        }

        public String getStringFor(ProxiedPlayer player) {
            return this.rawText.replaceAll("%player%", player.getName());
        }
    }

    public void start() {
        this.thisSchedule = ProxyServer.getInstance().getScheduler().schedule(GearzBungee.getInstance(), this, interval_seconds, interval_seconds, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            announce(proxiedPlayer);
        }
        this.current++;
        if (this.current == announcements.size()) this.current = 0;
    }

    private void reschedule() {
        cancel();
        start();
    }

    private void cancel() {
        if (this.thisSchedule != null) this.thisSchedule.cancel();
    }

    public void announce(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(GearzBungee.getInstance().getFormat("prefix", false, true) + ChatColor.translateAlternateColorCodes('&', announcements.get(this.current).getStringFor(proxiedPlayer)));
    }
}
