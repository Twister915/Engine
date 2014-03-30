package net.cogz.punishments;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import net.tbnr.gearz.activerecord.GModel;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * Gearz Punishments API
 * Supports Mutes, Bans,
 * and more types of
 * punishments
 */
public abstract class GearzPunishments {
    /**
     * Players that are muted
     */
    public Map<String, Punishment> mutedPlayers = new HashMap<>();

    private Map<String, List<Punishment>> allPunishments = new HashMap<>();

    private Map<String, Punishment> ipBans = new HashMap<>();
    /**
     * Database where punishments are stored
     */
    public DB database;

    /**
     * Gets the MongoDB from the server
     *
     * @return the server's database
     */
    public abstract DB getDB();

    /**
     * Kicks a player from the server
     *
     * @param player     player to kick
     * @param punishment punishment to kick for
     */
    public abstract void kickPlayer(String player, Punishment punishment);

    /**
     * Gets a list of a uuid's punishments
     *
     * @param uuid uuid to check
     * @param valid  valid punishments only
     * @return a list of punishments
     */
    public List<Punishment> getPunishmentsByUUID(String uuid, boolean valid) {
        Punishment punishment = new Punishment(getDB(), uuid);
        List<GModel> found = punishment.findAll();
        List<Punishment> punishments = new ArrayList<>();
        for (GModel m : found) {
            if (!(m instanceof Punishment)) continue;
            Punishment punishmentFound = (Punishment) m;
            if (valid && !punishmentFound.valid) continue;
            if (!punishmentFound.punished.equals(uuid)) continue;
            punishments.add(punishmentFound);
        }
        return punishments;
    }

    public List<Punishment> getPunishmentsByType(PunishmentType type, boolean valid) {
        Punishment punishment = new Punishment(getDB());
        List<GModel> found = punishment.findAll();
        List<Punishment> punishments = new ArrayList<>();
        for (GModel m : found) {
            if (!(m instanceof Punishment)) continue;
            Punishment punishmentFound = (Punishment) m;
            if (valid && !punishmentFound.valid) continue;
            if (type != punishmentFound.getPunishmentType()) continue;
            punishments.add(punishmentFound);
        }
        return punishments;
    }

    /**
     * Removes players punishments from list
     *
     * @param player player to remove
     */
    public void cleanUpPunishmentMap(String player) {
        if (this.allPunishments.containsKey(player)) {
            this.allPunishments.remove(player);
        }
    }

    /**
     * Checks whether or not a player is banned
     *
     * @param player player to check
     * @return whether or not the player is banned
     */
    public boolean isPlayerBanned(String player) {
        List<Punishment> punishments = getPunishmentsByUUID(player, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.PERMANENT_BAN && punishment.getPunishmentType() != PunishmentType.TEMP_BAN)
                continue;
            if (!punishment.valid) continue;
            PunishmentType type = punishment.getPunishmentType();
            if (type == PunishmentType.PERMANENT_BAN) return true;
            else if (new Date().before(punishment.end)) return true;
        }
        return false;
    }

    /**
     * Gets a players valid ban
     *
     * @param player player to get a ban for
     * @return latest ban for the player
     */
    public Punishment getValidBan(String player) {
        List<Punishment> punishments = getPunishmentsByUUID(player, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.PERMANENT_BAN && punishment.getPunishmentType() != PunishmentType.TEMP_BAN)
                continue;
            PunishmentType type = punishment.getPunishmentType();
            if (!punishment.valid) continue;
            if (type == PunishmentType.PERMANENT_BAN) {
                return punishment;
            } else if (new Date().before(punishment.end)) {
                return punishment;
            }
        }
        return null;
    }

    /**
     * Unbans a player
     *
     * @param player player to unban
     */
    public void unBan(String player) {
        Punishment punishment = getValidBan(player);
        punishment.valid = false;
        punishment.save();
    }

    /**
     * Checks if a player is muted
     *
     * @param player player to check
     * @return whether or not the player is muted
     */
    public boolean isPlayerMuted(String player) {
        List<Punishment> punishments = getPunishmentsByUUID(player, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.MUTE && punishment.getPunishmentType() != PunishmentType.TEMP_MUTE)
                continue;
            if (!punishment.valid) continue;
            PunishmentType type = punishment.getPunishmentType();
            if (type == PunishmentType.MUTE) return true;
            else if (new Date().before(punishment.end)) return true;
        }
        return false;
    }

    /**
     * Gets a player's latest and valid mute
     *
     * @param player player to get mute for
     * @return latest and valid mute for the player
     */
    public Punishment getValidMute(String player) {
        List<Punishment> punishments = getPunishmentsByUUID(player, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.MUTE && punishment.getPunishmentType() != PunishmentType.TEMP_MUTE)
                continue;
            PunishmentType type = punishment.getPunishmentType();
            if (!punishment.valid) continue;
            if (type == PunishmentType.MUTE) {
                return punishment;
            } else if (new Date().before(punishment.end)) {
                return punishment;
            }
        }
        return null;
    }

    /**
     * Unmutes a player
     *
     * @param player player to unmute
     */
    public void unMute(String player) {
        Punishment punishment = getValidMute(player);
        if (this.mutedPlayers.containsKey(player)) {
            this.mutedPlayers.remove(player);
        }
        punishment.valid = false;
        punishment.save();
    }

    /**
     * Loads a mute to the local list for a player
     *
     * @param player player to load mute for
     */
    public void loadMute(String player) {
        Punishment mute = getValidMute(player);
        if (mute == null) return;
        this.mutedPlayers.put(player, mute);
    }

    /**
     * Checks if a player is locally muted
     *
     * @param player player to check
     * @return whether or not a player is local muted
     */
    public boolean isPlayerLocalMuted(String player) {
        return this.mutedPlayers.containsKey(player);
    }

    /**
     * Returns a player's local mute
     *
     * @param player player to get the mute for
     * @return the player's local mute
     */
    public Punishment getLocalMute(String player) {
        Punishment punishment = this.mutedPlayers.get(player);
        if (punishment.getPunishmentType() != PunishmentType.MUTE && punishment.getPunishmentType() != PunishmentType.TEMP_MUTE) {
            return null;
        }
        PunishmentType type = punishment.getPunishmentType();
        if (!punishment.valid) {
            return null;
        }
        if (type == PunishmentType.MUTE) {
            return punishment;
        } else if (new Date().before(punishment.end)) {
            return punishment;
        }
        return null;
    }

    /**
     * Checks whether or not a player is IP banned
     *
     * @param ip ip to check for ban
     * @return whether or not a ip is banned
     */
    public boolean isIpBanned(String ip) {
        List<Punishment> punishments = getPunishmentsByUUID(ip, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.IP_BAN)
                continue;
            if (!punishment.valid) continue;
            return true;
        }
        return false;
    }

    /**
     * Gets the valid ban for an IP
     *
     * @param ip ip to
     * @return the ip's valid ban
     */
    public Punishment getValidIpBan(String ip) {
        List<Punishment> punishments = getPunishmentsByUUID(ip, true);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() != PunishmentType.IP_BAN) {
                continue;
            }
            if (!punishment.valid) continue;
            return punishment;

        }
        return null;
    }

    public boolean isLocalIpBanned(String ip) {
        return this.ipBans.containsKey(ip);
    }

    public Punishment getValidLocalIpBan(String ip) {
        return this.ipBans.get(ip);
    }

    /**
     * Unbans an IP
     *
     * @param ip ip to unban
     */
    public void unIpBan(String ip) {
        Punishment punishment = getValidIpBan(ip);
        punishment.valid = false;
        this.ipBans.remove(ip);
        punishment.save();
    }

    /**
     * Called when a player joins the server
     *
     * @param uuid player that joined
     * @return whether or not a player is banned
     */
    public boolean onJoin(String uuid) {
        return isPlayerBanned(uuid);
    }

    /**
     * Called when a player leaves the server
     * Manages removal of local mutes
     *
     * @param uuid player that left
     */
    public void onQuit(String uuid) {
        if (isPlayerLocalMuted(uuid)) {
            this.mutedPlayers.remove(uuid);
        }
    }

    /**
     * Called when a player chats
     *
     * @param player player that chatted
     * @return whether or not a player is muted
     */
    public boolean onChat(String player) {
        return isPlayerLocalMuted(player);
    }

    /**
     * Punishes a player with the specific parameters
     *
     * @param player uuid to punish
     * @param issuer uuid of the issuer of the punishment
     * @param reason reason for punishment
     * @param type   type of punishment
     * @param end    when the punishment ends
     */
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
        if (punishment.getPunishmentType().isKickable()) {
            kickPlayer(player, punishment);
        }
        if (type == PunishmentType.MUTE || type == PunishmentType.TEMP_MUTE) {
            this.mutedPlayers.put(player, punishment);
        }
        if (type == PunishmentType.IP_BAN) {
            this.ipBans.put(player, punishment);
        }
        punishment.save();
    }

    /**
     * Appeals a punishment for a player
     * Essentially invalidates it
     *
     * @param punishment punishment to invalidate
     */
    public void appealPunishment(Punishment punishment) {
        punishment.valid = false;
        punishment.save();
        PunishmentType type = punishment.getPunishmentType();
        if (type == PunishmentType.MUTE || type == PunishmentType.TEMP_MUTE) {
            this.mutedPlayers.remove(punishment.punished);
        }
        if (type == PunishmentType.IP_BAN) {
            this.ipBans.remove(punishment.punished);
        }
    }

    public void loadIpBans() {
        for (Punishment punishment : getPunishmentsByType(PunishmentType.IP_BAN, true)) {
            this.ipBans.put(punishment.punished, punishment);
        }
    }
}
