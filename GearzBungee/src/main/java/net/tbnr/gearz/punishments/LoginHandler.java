/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.punishments;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.Getter;
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
 *
 * Purpose Of File: Handles logins that may have a ip ban attached
 *
 * Latest Change:
 */
public class LoginHandler implements Listener {
    public final SimpleDateFormat longReadable = new SimpleDateFormat("MM/dd/yyyy hh:mm zzzz");

    @EventHandler(priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onPlayerLogin(PreLoginEvent event) {
        GearzPlayer gearzPlayer;
        try {
            gearzPlayer = new GearzPlayer(event.getConnection().getName());
        } catch (GearzPlayer.PlayerNotFoundException e) {
            return;
        }
        BasicDBObject activeBan = gearzPlayer.getActiveBan();
        if (activeBan == null) {
            DBObject ipBan = GearzBungee.getInstance().getIpBanHandler().getBanObject(event.getConnection().getAddress().getHostString());
            if (ipBan != null) {
                String reason = (String) ipBan.get("reason");
                String issuer = (String) ipBan.get("issuer");
                event.getConnection().disconnect(GearzBungee.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", reason}, new String[]{"<issuer>", issuer}));
                return;
            }
            MuteData muteData = gearzPlayer.getActiveMuteData();
            if (muteData != null) {
                GearzBungee.getInstance().getChat().addMute(event.getConnection().getName(), muteData);
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
        @Getter
        final
        Date end;

        @Getter
        final
        PunishmentType punishmentType;

        @Getter
        final
        boolean perm;

        @Getter
        final
        String reason;

        @Getter
        final
        String issuer;

        public MuteData(Date end, PunishmentType punishmentType, String reason, String issuer) {
            this.end = end;
            this.punishmentType = punishmentType;
            this.perm = (punishmentType == PunishmentType.MUTE);
            this.reason = reason;
            this.issuer = issuer;
        }
    }
}
