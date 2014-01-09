package net.tbnr.gearz.punishments;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.player.bungee.GearzPlayer;

import java.util.Date;

/**
 * Created by jake on 1/4/14.
 */
public class LoginHandler implements Listener {
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
            DBObject ipBan = GearzBungee.getInstance().getIpBanHandler().getBanObject(event.getConnection().getAddress().getHostName());
            if (ipBan != null) {
                String reason = (String) ipBan.get("reason");
                String issuer = (String) ipBan.get("issuer");
                event.getConnection().disconnect(GearzBungee.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", reason}, new String[]{"<issuer>", issuer}));
            }
            MuteData muteData = gearzPlayer.getActiveMuteData();
            if (muteData != null) {
                GearzBungee.getInstance().getChat().addMute(gearzPlayer.getProxiedPlayer(), muteData);
            }
            return;
        }
        String reason = activeBan.getString("reason");

        event.getConnection().disconnect(GearzBungee.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", reason}));
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
