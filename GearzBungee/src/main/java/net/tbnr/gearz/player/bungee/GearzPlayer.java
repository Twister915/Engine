package net.tbnr.gearz.player.bungee;

import com.mongodb.*;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.punishments.LoginHandler;
import net.tbnr.gearz.punishments.PunishmentType;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Used to access the database, disconnect, etc.
 */
public class GearzPlayer {
    /**
     * The player's username
     */
    private String username;
    /**
     * The player document
     */
    private DBObject playerDocument;
    /**
     * Date on players mute
     */
    private LoginHandler.MuteData muteData;

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

    public void punishPlayer(String reason, GearzPlayer issuer, PunishmentType punishmentType, Date end, boolean console) {
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
        DBObject dbObject = this.getPlayerDocument();
        for (String string : dbObject.keySet()) {
            ProxyServer.getInstance().getLogger().info(string + ":" + dbObject.get(string));
        }
        Object bansl = dbObject.get("punishments");
        if (!(bansl instanceof BasicDBList)) {
            if (dbObject.get("punishments") == null) {
                ProxyServer.getInstance().getLogger().info("Still null!");
            }
            bansl = new BasicDBList();
        }
        ProxyServer.getInstance().getLogger().info("_______________");

        BasicDBList bans = (BasicDBList) bansl;
        for (String string : bans.keySet()) {
            ProxyServer.getInstance().getLogger().info(string + ":" + bans.get(string));
        }
        bans.add(ban);
        dbObject.put("punishments", bans);
        getCollection().save(dbObject);
        String name = (console ? "CONSOLE" : issuer.getName());
        if (punishmentType.isKickable() && getProxiedPlayer() != null) {
            kickPlayer(GearzBungee.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", reason}), name);
        }
    }

    public void punishPlayer(String reason, GearzPlayer issuer, PunishmentType punishmentType, boolean console) {
        punishPlayer(reason, issuer, punishmentType, new Date(), console);
    }

    public void kickPlayer(String reason, String issuer) {
        this.getProxiedPlayer().disconnect(GearzBungee.getInstance().getFormat("kick", false, true, new String[]{"<reason>", reason}, new String[]{"<issuer>", issuer}));
    }

    public void unban() {
        getActiveBan().put("valid", false);
        save();
    }

    public void unMute() {
        getActiveMute().put("valid", false);
        this.muteData = null;
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
            ProxyServer.getInstance().getLogger().info("this called");
            if (!(o instanceof BasicDBObject)) continue;
            ProxyServer.getInstance().getLogger().info("dis this get called");
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
                    if (new Date().before(end)) return null;
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
                if (punishmentType == PunishmentType.MUTE) {
                    return new LoginHandler.MuteData(new Date(), PunishmentType.MUTE, true, mute.getString("issuer"), mute.getString("reason"));
                } else if (punishmentType == PunishmentType.TEMP_MUTE) {
                    Date end = mute.getDate("end");
                    if (new Date().before(end)) return null;
                    return new LoginHandler.MuteData(end, PunishmentType.TEMP_MUTE, false, mute.getString("issuer"), mute.getString("reason"));
                }
            }
        }
        return null;
    }

    public LoginHandler.MuteData getMuteData() {
        return muteData;
    }

    public void setMuteData(LoginHandler.MuteData muteData) {
        this.muteData = muteData;
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

    public boolean isMuted() {
        if (muteData == null) return false;
        if (muteData.isPerm()) return true;
        Date end = muteData.getEnd();
        return new Date().before(end);
    }
}