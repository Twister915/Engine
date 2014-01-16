package net.tbnr.gearz.modules;

import com.mongodb.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jake on 1/15/14.
 */
public class ReportModule implements TCommandHandler {
    public SimpleDateFormat readable = new SimpleDateFormat("MM/dd/yyyy");
    ReportManager reportManager;
    public ReportModule(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    @TCommand(name = "report", permission = "gearz.report", senders = {TCommandSender.Player}, usage = "/report <player> <message...>")
    @SuppressWarnings({"unused", "deprecation"})
    public TCommandStatus report(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length < 2) {
            return TCommandStatus.INVALID_ARGS;
        }
        // Must be player, no console
        ProxiedPlayer reporter = (ProxiedPlayer) sender;
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target == null) {
            return TCommandStatus.INVALID_ARGS;
        }
        GearzPlayer gearzReporter;
        GearzPlayer gearzTarget;
        try {
            gearzReporter = new GearzPlayer(reporter);
            gearzTarget = new GearzPlayer(target);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            return TCommandStatus.INVALID_ARGS;
        }
        String reason = GearzBungee.getInstance().compile(args, 2, args.length);
        Date time = new Date();
        String bungeeServer = target.getServer().getInfo().getName();
        Report report = new Report(gearzReporter, gearzTarget, reason, bungeeServer, time);
        this.reportManager.addReport(report);
        sender.sendMessage(GearzBungee.getInstance().getFormat("reported", false, false, new String[]{"<player>", target.getName()}));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "reports", permission = "gearz.report.view", senders = {TCommandSender.Player, TCommandSender.Console}, usage = "/reports <amount>")
    @SuppressWarnings({"unused", "deprecation"})
    public TCommandStatus reports(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length > 1) {
            return TCommandStatus.MANY_ARGS;
        }

        Integer amount = 10;
        if (args.length == 1) {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return TCommandStatus.INVALID_ARGS;
            }
        }

        for (Report report : this.reportManager.getRecentReports(amount, null)) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("reports-format", false, false, new String[]{"<reason>", report.getMessage()}, new String[]{"<reporter>", report.getReporter().getName()}, new String[]{"<reporter>", report.reported.getName()}, new String[]{"<time>", readable.format(report.getTime())}));
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "preports", aliases = "playerreports", permission = "gearz.report.view", senders = {TCommandSender.Player, TCommandSender.Console}, usage = "/reports <amount>")
    @SuppressWarnings({"unused", "deprecation"})
    public TCommandStatus preports(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length != 1) {
            return TCommandStatus.MANY_ARGS;
        }

        GearzPlayer target;
        try {
            target = new GearzPlayer(args[0]);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            return TCommandStatus.INVALID_ARGS;
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("preports-header", false, false, new String[]{"<player>", target.getName()}));
        for (Report report : this.reportManager.getRecentReports(10, target)) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("preports-format", false, false, new String[]{"<reason>", report.getMessage()}, new String[]{"<reporter>", report.getReporter().getName()}, new String[]{"<time>", readable.format(report.getTime())}));
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    public static class Report {
        GearzPlayer reporter;
        GearzPlayer reported;
        String message;
        String bungeeServer;
        Date time;

        public Report(GearzPlayer reporter, GearzPlayer reported, String message, String bungeeServer, Date time) {
            this.reporter = reporter;
            this.reported = reported;
            this.message = message;
            this.bungeeServer = bungeeServer;
            this.time = time;
        }

        public GearzPlayer getReporter() {
            return reporter;
        }

        public GearzPlayer getReported() {
            return reported;
        }

        public String getMessage() {
            return message;
        }

        public String getBungeeServer() {
            return bungeeServer;
        }

        public Date getTime() {
            return time;
        }

        public void broadcast() {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.hasPermission("gearz.reports.recieve")) {
                    player.sendMessage(GearzBungee.getInstance().getFormat("report-recieve", false, false, new String[]{"<reported>", getReported().getName()}, new String[]{"<reporter>", getReporter().getName()}, new String[]{"<reason>", getMessage()}, new String[]{"<server>", getBungeeServer()}));
                }
            }
        }

        @Override
        public String toString() {
            return "Report{" +
                    "reporter='" + reporter.getName() + '\'' +
                    ", reported='" + reported.getName() + '\'' +
                    ", message='" + message + '\'' +
                    ", bungeeServer='" + bungeeServer + '\'' +
                    ", time=" + time +
                    '}';
        }
    }

    public static class ReportManager {
        DBCollection reportCollection;
        public ReportManager(DBCollection collection) {
            this.reportCollection = collection;
        }

        public List<Report> getRecentReports(Integer amount, GearzPlayer gearzPlayer) {
            List<Report> reports = new ArrayList<>();
            DBCursor dbCursor;
            if (gearzPlayer != null) {
                dbCursor = reportCollection.find(new BasicDBObject("reported", gearzPlayer.getPlayerDocument().get("_id"))).limit(amount);
            } else {
                dbCursor = reportCollection.find().limit(amount);
            }
            while (dbCursor.hasNext()) {
                DBObject report = dbCursor.next();
                GearzPlayer reporter;
                GearzPlayer reported;
                try {
                    reporter = GearzPlayer.getById((ObjectId) report.get("reporter"));
                    reported = GearzPlayer.getById((ObjectId) report.get("reported"));
                } catch (GearzPlayer.PlayerNotFoundException e) {
                    continue;
                }
                String reason = (String) report.get("reason");
                Date date = (Date) report.get("time");
                String server = (String) report.get("server");
                Report returned = new Report(reporter, reported, reason, server, date);
                reports.add(returned);
            }
            return reports;
        }

        public void addReport(Report report) {
            BasicDBObject basicDBObject =
                    new BasicDBObject("reporter", report.getReporter().getPlayerDocument().get("_id")).
                            append("reported", report.getReported().getPlayerDocument().get("_id")).
                            append("message", report.getMessage()).
                            append("server", report.getBungeeServer()).
                            append("time", report.getTime());
            reportCollection.insert(basicDBObject);
        }
    }
}
