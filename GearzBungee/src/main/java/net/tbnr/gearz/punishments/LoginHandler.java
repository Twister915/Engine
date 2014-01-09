package net.tbnr.gearz.punishments;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.player.bungee.GearzPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jake on 1/4/14.
 */
public class LoginHandler implements Listener {
    public SimpleDateFormat longReadable = new SimpleDateFormat("MM/dd/yyyy hh:mm zzzz");

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PreLoginEvent event) {
        GearzPlayer gearzPlayer;
        try {
            gearzPlayer = new GearzPlayer(event.getConnection().getName());
        } catch (GearzPlayer.PlayerNotFoundException e) {
            return;
        }
        BasicDBObject activeBan = gearzPlayer.getActiveBan();
        if (activeBan == null) {
            ProxyServer.getInstance().getLogger().info(event.getConnection().getAddress().getHostString());
            DBObject ipBan = GearzBungee.getInstance().getIpBanHandler().getBanObject(event.getConnection().getAddress().getHostString());
            if (ipBan != null) {
                String reason = (String) ipBan.get("reason");
                String issuer = (String) ipBan.get("issuer");
                event.getConnection().disconnect(GearzBungee.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", reason}, new String[]{"<issuer>", issuer}));
                return;
            }
            MuteData muteData = gearzPlayer.getActiveMuteData();
            ProxyServer.getInstance().getLogger().info("NULL");
            if (muteData != null) {
                ProxyServer.getInstance().getLogger().info("NOT NULL");

                GearzBungee.getInstance().getChat().addMute(gearzPlayer.getProxiedPlayer(), muteData);
            }
            return;
        }
        String reason = activeBan.getString("reason");

        PunishmentType punishmentType = PunishmentType.valueOf(activeBan.getString("type"));
        if (punishmentType == PunishmentType.PERMANENT_BAN) {
            event.getConnection().disconnect(GearzBungee.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", reason}));
        } else if (punishmentType == PunishmentType.TEMP_BAN) {
            Date end = activeBan.getDate("end");
            event.getConnection().disconnect(GearzBungee.getInstance().getFormat("temp-reason", false, true, new String[]{"<reason>", reason}, new String[]{"<date>", longReadable.format(end)}));
        }
    }

        public static class MuteData {
        Date end;
        PunishmentType punishmentType;
        boolean perm;
        String reason;
        String issuer;

        public MuteData(Date end, PunishmentType punishmentType, String reason, String issuer) {
            this.end = end;
            this.punishmentType = punishmentType;
            this.perm = (punishmentType == PunishmentType.MUTE);
            this.reason = reason;
            this.issuer = issuer;
        }

        public Date getEnd() {
            return end;
        }

        public boolean isPerm() {
            return perm;
        }

        public String getReason() {
            return reason;
        }

        public String getIssuer() {
            return issuer;
        }
    }
}
