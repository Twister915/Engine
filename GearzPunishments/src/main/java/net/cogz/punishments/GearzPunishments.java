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

package net.cogz.punishments;

import com.mongodb.DB;
import net.tbnr.gearz.activerecord.GModel;

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
    public Map<String, Punishment> mutedUUIDs = new HashMap<>();

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
     * Kicks a uuid from the server
     *
     * @param uuid     uuid to kick
     * @param punishment punishment to kick for
     */
    public abstract void kickPlayer(String uuid, Punishment punishment);

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
     * @param uuid uuid to remove
     */
    public void cleanUpPunishmentMap(String uuid) {
        if (this.allPunishments.containsKey(uuid)) {
            this.allPunishments.remove(uuid);
        }
    }

    /**
     * Checks whether or not a uuid is banned
     *
     * @param uuid uuid to check
     * @return whether or not the uuid is banned
     */
    public boolean isUUIDBanned(String uuid) {
        List<Punishment> punishments = getPunishmentsByUUID(uuid, true);
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
     * @param uuid uuid to get a ban for
     * @return latest ban for the uuid
     */
    public Punishment getValidBan(String uuid) {
        List<Punishment> punishments = getPunishmentsByUUID(uuid, true);
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
     * Unbans a uuid
     *
     * @param uuid uuid to unban
     */
    public void unBan(String uuid) {
        Punishment punishment = getValidBan(uuid);
        punishment.valid = false;
        punishment.save();
    }

    /**
     * Checks if a uuid is muted
     *
     * @param uuid uuid to check
     * @return whether or not the uuid is muted
     */
    public boolean isPlayerMuted(String uuid) {
        List<Punishment> punishments = getPunishmentsByUUID(uuid, true);
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
     * Gets a uuid's latest and valid mute
     *
     * @param uuid uuid to get mute for
     * @return latest and valid mute for the uuid
     */
    public Punishment getValidMute(String uuid) {
        List<Punishment> punishments = getPunishmentsByUUID(uuid, true);
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
     * Unmutes a uuid
     *
     * @param uuid uuid to unmute
     */
    public void unMute(String uuid) {
        Punishment punishment = getValidMute(uuid);
        if (this.mutedUUIDs.containsKey(uuid)) {
            this.mutedUUIDs.remove(uuid);
        }
        punishment.valid = false;
        punishment.save();
    }

    /**
     * Loads a mute to the local list for a uuid
     *
     * @param uuid uuid to load mute for
     */
    public void loadMute(String uuid) {
        Punishment mute = getValidMute(uuid);
        if (mute == null) return;
        this.mutedUUIDs.put(uuid, mute);
    }

    /**
     * Checks if a uuid is locally muted
     *
     * @param uuid uuid to check
     * @return whether or not a uuid is local muted
     */
    public boolean isUUIDLocalMuted(String uuid) {
        return this.mutedUUIDs.containsKey(uuid);
    }

    /**
     * Returns a uuid's local mute
     *
     * @param uuid uuid to get the mute for
     * @return the uuid's local mute
     */
    public Punishment getLocalMute(String uuid) {
        Punishment punishment = this.mutedUUIDs.get(uuid);
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
     * @param uuid UUID of the player that joined
     * @return whether or not a UUID is banned
     */
    public boolean onJoin(String uuid) {
        return isUUIDBanned(uuid);
    }

    /**
     * Called when a player leaves the server
     * Manages removal of local mutes
     *
     * @param uuid player that left
     */
    public void onQuit(String uuid) {
        if (isUUIDLocalMuted(uuid)) {
            this.mutedUUIDs.remove(uuid);
        }
    }

    /**
     * Called when a player chats
     *
     * @param uuid uuid of the player that chatted
     * @return whether or not a uuid is muted
     */
    public boolean onChat(String uuid) {
        return isUUIDLocalMuted(uuid);
    }

    /**
     * Punishes a player with the specific parameters.
     * UUID of the console is CONSOLE
     *
     * @param punishedUUID uuid of the punished player
     * @param issuerUUID uuid of the issuer of the punishment
     * @param reason reason for punishment
     * @param type   type of punishment
     * @param end    when the punishment ends
     */
    public void punishPlayer(String punishedUUID, String issuerUUID, String reason, PunishmentType type, Date end) {
        Punishment punishment = new Punishment(this.database);
        punishment.punished = punishedUUID;
        punishment.issuer = issuerUUID;
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
            kickPlayer(punishedUUID, punishment);
        }
        if (type == PunishmentType.MUTE || type == PunishmentType.TEMP_MUTE) {
            this.mutedUUIDs.put(punishedUUID, punishment);
        }
        if (type == PunishmentType.IP_BAN) {
            this.ipBans.put(punishedUUID, punishment);
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
            this.mutedUUIDs.remove(punishment.punished);
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
