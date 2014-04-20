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

package net.tbnr.gearz.server;

import com.mongodb.DB;
import com.mongodb.DBObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;

import java.util.List;

/**
 * Object to store information about
 * a Gearz server. Allows for a player
 * to be connected to a server based
 * on the bungee_name, which is the
 * name of the server in the Bungeecord
 * Proxy.
 *
 * <p>
 * Latest Change: Add online players
 * <p>
 *
 * @author Joey
 * @since 12/17/2013
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false, of = {"game", "bungee_name", "port", "address"})
public final class Server extends GModel {
    @BasicField
    private String game; //Current game being played
    @BasicField
    private String bungee_name; //Name of the server as stored in the Bungee Proxy
    @BasicField
    private Integer number; //Number of this game type
    @BasicField
    private String statusString; //Current status: lobby, map-loading, etc
    @BasicField
    private boolean canJoin; //Whether or not this server is joinable
    @BasicField
    private Integer playerCount; //Current number of players online
    @BasicField
    private String address; //IP of the server
    @BasicField
    private Integer port; //Port of the server
    @BasicField
    private Integer maximumPlayers; //Max number players that can join this server
    @BasicField
    private List<String> onlinePlayers; //A list of online players

    public Server() {
        super();
    }

    public Server(DB database) {
        super(database);
    }

    public Server(DB database, DBObject dBobject) {
        super(database, dBobject);
    }

    /**
     * Returns a non-null player count
     *
     * @return the server's player count, 0 if null
     */
    public Integer getPlayerCount() {
        if (this.playerCount == null) return 0;
        return this.playerCount;
    }
}