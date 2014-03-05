package net.cogz.punishments;

import com.mongodb.DB;
import net.tbnr.gearz.activerecord.GModel;

import java.util.*;

/**
 * Created by jake on 3/4/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public abstract class GearzPunishments {
    List<String> mutedPlayers = new ArrayList<>();
    public DB database;

    public abstract DB getDB();

    public abstract void kickPlayer(String player, Punishment punishment);

    public List<Punishment> getPunishmentsByPlayer(String player, boolean valid) {
        Punishment punishment = new Punishment(getDB(), player);
        List<GModel> found = punishment.findAll();
        List<Punishment> punishments = new ArrayList<>();
        for (GModel m : found) {
            if (!(m instanceof Punishment)) continue;
            Punishment punishmentFound = (Punishment) m;
            if (valid && !punishmentFound.valid) continue;
            if (!punishmentFound.punished.equals(player)) continue;
            punishments.add(punishmentFound);
        }
        return punishments;
    }

    public boolean isPlayerBanned(String player) {
        List<Punishment> punishments = getPunishmentsByPlayer(player, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.PERMANENT_BAN && punishment.getPunishmentType() != PunishmentType.TEMP_BAN)
                continue;
            if (!punishment.valid) continue;
            PunishmentType type = punishment.getPunishmentType();
            if (type == PunishmentType.PERMANENT_BAN) return true;
            else if (type == PunishmentType.TEMP_BAN && new Date().before(punishment.end)) return true;
        }
        return false;
    }

    public Punishment getValidBan(String player) {
        List<Punishment> punishments = getPunishmentsByPlayer(player, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.PERMANENT_BAN && punishment.getPunishmentType() != PunishmentType.TEMP_BAN)
                continue;
            PunishmentType type = punishment.getPunishmentType();
            if (!punishment.valid) continue;
            if (type == PunishmentType.PERMANENT_BAN) {
                return punishment;
            } else if (type == PunishmentType.TEMP_BAN && new Date().before(punishment.end)) {
                return punishment;
            }
        }
        return null;
    }

    public void unBan(String player) {
        Punishment punishment = getValidBan(player);
        punishment.valid = false;
        punishment.save();
    }

    public boolean isPlayerMuted(String player) {
        List<Punishment> punishments = getPunishmentsByPlayer(player, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.MUTE && punishment.getPunishmentType() != PunishmentType.TEMP_MUTE)
                continue;
            if (!punishment.valid) continue;
            PunishmentType type = punishment.getPunishmentType();
            if (type == PunishmentType.MUTE) return true;
            else if (type == PunishmentType.TEMP_MUTE && new Date().before(punishment.end)) return true;
        }
        return false;
    }

    public Punishment getValidMute(String player) {
        List<Punishment> punishments = getPunishmentsByPlayer(player, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.MUTE && punishment.getPunishmentType() != PunishmentType.TEMP_MUTE)
                continue;
            PunishmentType type = punishment.getPunishmentType();
            if (!punishment.valid) continue;
            if (type == PunishmentType.MUTE) {
                return punishment;
            } else if (type == PunishmentType.TEMP_MUTE && new Date().before(punishment.end)) {
                return punishment;
            } else {
                if (this.mutedPlayers.contains(player)) {
                    this.mutedPlayers.remove(player);
                }
            }
        }
        return null;
    }

    public void unMute(String player) {
        Punishment punishment = getValidMute(player);
        punishment.valid = false;
        punishment.save();
    }

    public void loadMute(String player) {
        Punishment mute = getValidMute(player);
        if (mute == null) return;
        this.mutedPlayers.add(player);
    }

    public boolean isPlayerLocalMuted(String player) {
        return this.mutedPlayers.contains(player);
    }

    public boolean isIpBanned(String ip) {
        List<Punishment> punishments = getPunishmentsByPlayer(ip, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.IP_BAN)
                continue;
            if (!punishment.valid) continue;
            return true;
        }
        return false;
    }

    public Punishment getValidIpBan(String ip) {
        List<Punishment> punishments = getPunishmentsByPlayer(ip, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.IP_BAN)
                continue;
            if (!punishment.valid) continue;
            return punishment;

        }
        return null;
    }

    public void unIpBan(String ip) {
        Punishment punishment = getValidIpBan(ip);
        punishment.valid = false;
        punishment.save();
    }

    public boolean onJoin(String player) {
        return isPlayerBanned(player);
    }

    public void onQuit(String player) {
        if (isPlayerLocalMuted(player)) {
            this.mutedPlayers.remove(player);
        }
    }

    public boolean onChat(String player) {
        return isPlayerLocalMuted(player);
    }

    public void punishPlayer(String player, String issuer, String reason, PunishmentType type, Date end) {
        Punishment punishment = new Punishment(this.database);
        punishment.punished = player;
        punishment.issuer = issuer;
        punishment.reason = reason;
        punishment.valid = true;
        punishment.type = type.toString();
        if (end == null) {
            punishment.end = new Date();
        } else {
            punishment.end = end;
        }
        punishment.time = new Date();
        punishment.save();
        if (punishment.getPunishmentType().isKickable()) {
            kickPlayer(player, punishment);
        }
        if (type == PunishmentType.MUTE || type == PunishmentType.TEMP_MUTE) {
            this.mutedPlayers.add(player);
        }
    }

    public void appealPunishment(Punishment punishment) {
        punishment.valid = false;
        punishment.save();
        PunishmentType type = punishment.getPunishmentType();
        if (type == PunishmentType.MUTE || type == PunishmentType.TEMP_MUTE) {
            this.mutedPlayers.remove(punishment.punished);
        }
    }
}
