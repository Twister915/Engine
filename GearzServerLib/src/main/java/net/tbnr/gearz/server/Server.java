package net.tbnr.gearz.server;

import com.mongodb.DB;
import com.mongodb.DBObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;

/**
 * Created by Joey on 12/17/13.
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class Server extends GModel {
    @BasicField
    private String game;
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