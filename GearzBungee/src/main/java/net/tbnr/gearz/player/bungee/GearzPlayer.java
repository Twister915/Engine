package net.tbnr.gearz.player.bungee;

import com.mongodb.*;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.punishments.LoginHandler;
import net.tbnr.gearz.punishments.PunishmentType;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Used to access the database, disconnect, etc.
 */
public final class GearzPlayer {
    /**
     * The player's username
     */
    private final String username;
    /**
     * The player document
     */
    private DBObject playerDocument;

    private GearzPlayer(@NonNull DBObject object) throws PlayerNotFoundException {
        String username1;
        try {
            username1 = (String) object.get("username");
        } catch (ClassCastException ex) {
            throw new PlayerNotFoundException("Invalid document");
        }
        this.playerDocument = object;
        this.username = username1;
    }

    /**
     * Creates a player from a proxied player
     *
     * @param player The proxied player. :o
     */

    public GearzPlayer(String player) throws PlayerNotFoundException {
        this.username = player;
        loadDocument();
    }

    public GearzPlayer(ProxiedPlayer player) throws PlayerNotFoundException {
        this(player.getName());
    }

    public static GearzPlayer getById(ObjectId id) throws PlayerNotFoundException {
        DBObject query = new BasicDBObject("_id", id);
        DBObject one = getCollection().findOne(query);
        if (one == null) throw new PlayerNotFoundException("Invalid ID");
        return new GearzPlayer(one);
    }

    /**
     * Loads the document from the database representing the player :D
     *
     * @throws PlayerNotFoundException This occurs when there is no database object for that player, can be used as a hook
     *                                 for retrying the find.
     */
    private void loadDocument() throws PlayerNotFoundException {
        DBObject object = new BasicDBObject("username", this.username);
        DBObject cursor = getCollection().findOne(object);
        if (cursor != null) {
            this.playerDocument = cursor;
        } else {
            throw new PlayerNotFoundException("Player not found yet!");
        }
    }

    /**
     * Gets the base collection.
     *
     * @return Collection
     */
    public static DBCollection getCollection() {
        return GearzBungee.getInstance().getMongoDB().getCollection(GearzBungee.getInstance().getFormat("db-collection", false, false));
    }

    /**
     * Player Not Found Exception
     */
    public static class PlayerNotFoundException extends Exception {
        public PlayerNotFoundException(String s) {
            super(s);
        }
    }

    public DBObject getPlayerDocument() {
        if (this.playerDocument != null) return this.playerDocument;
        try {
            loadDocument();
        } catch (PlayerNotFoundException e) {
            return null;
        }
        return this.playerDocument;
    }

    public ProxiedPlayer getProxiedPlayer() {
        return ProxyServer.getInstance().getPlayer(this.username);
    }

    public final SimpleDateFormat longReadable = new SimpleDateFormat("MM/dd/yyyy hh:mm zzzz");

    public void punishPlayer(final String reason, final GearzPlayer issuer, final PunishmentType punishmentType, final Date end, final boolean console) {
        if (getPlayerDocument() == null) return;

        ObjectId objectId = null;
        if (!console) objectId = (ObjectId) issuer.getPlayerDocument().get("_id");

        DBObject ban = new BasicDBObjectBuilder().
                add("issuer", (console ? "CONSOLE" : objectId)).
                add("valid", true).
                add("reason", reason).
                add("type", punishmentType.toString()).
                add("time", new Date()).
                add("end", end).get();
        DBObject dbObject = getPlayerDocument();

        Object bansl = dbObject.get("punishments");
        if (!(bansl instanceof BasicDBList)) {
            bansl = new BasicDBList();
        }
        BasicDBList bans = (BasicDBList) bansl;
        bans.add(ban);
        dbObject.put("punishments", bans);
        getCollection().save(dbObject);
        save();

        String name = (console ? "CONSOLE" : issuer.getName());
        if ((punishmentType == PunishmentType.MUTE || punishmentType == PunishmentType.TEMP_MUTE) && getProxiedPlayer() != null) {
            LoginHandler.MuteData muteData = new LoginHandler.MuteData(end, punishmentType, reason, name);
            GearzBungee.getInstance().getChat().addMute(getName(), muteData);
        }

        if (punishmentType.isKickable() && getProxiedPlayer() != null) {
            if (punishmentType == PunishmentType.PERMANENT_BAN) {
                kickPlayer(GearzBungee.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", reason}), name);
            } else if (punishmentType == PunishmentType.TEMP_BAN) {
                kickPlayer(GearzBungee.getInstance().getFormat("temp-reason", false, true, new String[]{"<reason>", reason}, new String[]{"<date>", longReadable.format(end)}), name);
            } else if (punishmentType == PunishmentType.KICK) {
                kickPlayer(GearzBungee.getInstance().getFormat("kick-reason", false, true, new String[]{"<reason>", reason}), name);
            }
            kickPlayer(GearzBungee.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", reason}), name);
        }

    }

    public void punishPlayer(String reason, GearzPlayer issuer, PunishmentType punishmentType, boolean console) {
        punishPlayer(reason, issuer, punishmentType, new Date(), console);
    }

    public void kickPlayer(String reason, String issuer) {
        if (this.getProxiedPlayer() == null) return;
        this.getProxiedPlayer().disconnect(GearzBungee.getInstance().getFormat("kick", false, true, new String[]{"<reason>", reason}, new String[]{"<issuer>", issuer}));
    }

    public void unban() {
        getActiveBan().put("valid", false);
        save();
    }

    public void unMute() {
        getActiveMute().put("valid", false);
        GearzBungee.getInstance().getChat().removeMute(getName());
        save();
    }

    public void appealPunishment(BasicDBObject punishment) {
        punishment.put("valid", false);
        save();
    }

    public BasicDBObject getActiveBan() {
        try {
            loadDocument();
        } catch (PlayerNotFoundException e) {
            return null;
        }
        Object punishmentsl = getPlayerDocument().get("punishments");
        if (punishmentsl == null || !(punishmentsl instanceof BasicDBList)) {
            return null;
        }
        BasicDBList punishment = (BasicDBList) punishmentsl;
        for (Object o : punishment) {
            if (!(o instanceof BasicDBObject)) continue;
            BasicDBObject ban = (BasicDBObject) o;
            PunishmentType punishmentType = PunishmentType.valueOf(ban.getString("type"));
            if (punishmentType == PunishmentType.TEMP_BAN && ban.getBoolean("valid")) {
                Date end = ban.getDate("end");
                if (new Date().before(end)) {
                    return ban;
                }
            }
            if (punishmentType == PunishmentType.PERMANENT_BAN && ban.getBoolean("valid")) {
                return ban;
            }
        }
        return null;
    }

    public BasicDBObject getActiveMute() {
        try {
            loadDocument();
        } catch (PlayerNotFoundException e) {
            return null;
        }
        Object punishmentsl = getPlayerDocument().get("punishments");
        if (punishmentsl == null || !(punishmentsl instanceof BasicDBList)) {
            return null;
        }
        BasicDBList punishment = (BasicDBList) punishmentsl;
        for (Object o : punishment) {
            if (!(o instanceof BasicDBObject)) continue;
            BasicDBObject mute = (BasicDBObject) o;
            PunishmentType punishmentType = PunishmentType.valueOf(mute.getString("type"));
            if (mute.getBoolean("valid")) {
                if (punishmentType == PunishmentType.MUTE) {
                    return mute;
                } else if (punishmentType == PunishmentType.TEMP_MUTE) {
                    Date end = mute.getDate("end");
                    if (!new Date().before(end)) return null;
                    return mute;
                }
            }
        }
        return null;
    }

    public LoginHandler.MuteData getActiveMuteData() {
        try {
            loadDocument();
        } catch (PlayerNotFoundException e) {
            return null;
        }
        Object punishmentsl = getPlayerDocument().get("punishments");
        if (punishmentsl == null || !(punishmentsl instanceof BasicDBList)) {
            return null;
        }
        BasicDBList punishment = (BasicDBList) punishmentsl;
        for (Object o : punishment) {
            if (!(o instanceof BasicDBObject)) continue;
            BasicDBObject mute = (BasicDBObject) o;
            PunishmentType punishmentType = PunishmentType.valueOf(mute.getString("type"));
            if (mute.getBoolean("valid")) {
                String issuer = "NULL";
                if (mute.get("issuer") instanceof String) {
                    issuer = "CONSOLE";
                } else if (mute.get("issuer") instanceof ObjectId) {
                    try {
                        issuer = getById(mute.getObjectId("issuer")).getName();
                    } catch (PlayerNotFoundException e) {
                        issuer = "NULL";
                    }
                }
                if (punishmentType == PunishmentType.MUTE) {
                    return new LoginHandler.MuteData(new Date(), PunishmentType.MUTE, mute.getString("reason"), issuer);
                } else if (punishmentType == PunishmentType.TEMP_MUTE) {
                    Date end = mute.getDate("end");
                    if (new Date().before(end)) return null;
                    return new LoginHandler.MuteData(end, PunishmentType.TEMP_MUTE, mute.getString("reason"), issuer);
                }
            }
        }
        return null;
    }

    public List<BasicDBObject> getPunishments() {
        List<BasicDBObject> punishments = new ArrayList<>();
        try {
            loadDocument();
        } catch (PlayerNotFoundException e) {
            return null;
        }
        Object punishmentsl = getPlayerDocument().get("punishments");
        if (punishmentsl == null || !(punishmentsl instanceof BasicDBList)) {
            return null;
        }
        BasicDBList punishment = (BasicDBList) punishmentsl;
        for (Object o : punishment) {
            if (!(o instanceof BasicDBObject)) continue;
            BasicDBObject p = (BasicDBObject) o;
            if (p.getBoolean("valid")) {
                punishments.add(p);
            }
        }
        return punishments;
    }

    public String getName() {
        return this.username;
    }

    private void save() {
        getCollection().save(this.playerDocument);
    }
}
