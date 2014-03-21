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
 * Created by Joey on 12/17/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false, of = {"game", "bungee_name", "port", "address"})
public final class Server extends GModel {
    @BasicField
    private String game;
    @BasicField
    private String shortGame;
    @BasicField
    private String bungee_name;
    @BasicField
    private Integer number;
    @BasicField
    private String statusString;
    @BasicField
    private boolean canJoin;
    @BasicField
    private Integer playerCount;
    @BasicField
    private String address;
    @BasicField
    private Integer port;
    @BasicField
    private Integer maximumPlayers;
    @BasicField
    private List<String> onlinePlayers;

    public Server() {
        super();
    }

    public Server(DB database) {
        super(database);
    }

    public Server(DB database, DBObject dBobject) {
        super(database, dBobject);
    }

    public Integer getPlayerCount() {
        if (this.playerCount == null) return 0;
        return this.playerCount;
    }
}