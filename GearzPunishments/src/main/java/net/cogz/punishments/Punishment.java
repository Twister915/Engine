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

package net.cogz.punishments;

import com.mongodb.DB;
import com.mongodb.DBObject;
import lombok.ToString;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;

import java.util.Date;

/**
 * Stores data about a punishment that a
 * player has receieved. Storage is based
 * on UUIDs and not player names.
 *
 * <p>
 * Latest Change: UUID Changes
 * <p>
 *
 * @author Jake
 * @since Unknown
 */
@ToString
public class Punishment extends GModel {
    @BasicField
    public String issuer; //uuid of issuer
    @BasicField
    public String punished; //uuid of punished player
    @BasicField
    public String reason; //reason for punishment
    @BasicField
    public Boolean valid; //whether or not the punishment was appealed
    @BasicField
    public String type; //the type of punishment, tored as the string of an enum
    @BasicField
    public Date time; //time punishment was given
    @BasicField
    public Date end; //ending time of punishments, optional

    @SuppressWarnings("unused")
    public Punishment() {
        super();
    }

    public Punishment(DB database) {
        super(database);
    }

    @SuppressWarnings("unused")
    public Punishment(DB database, DBObject dBobject) {
        super(database, dBobject);
    }

    /**
     * @param punished uuid to lookup
     */
    public Punishment(DB database, String punished) {
        this(database);
        this.punished = punished;
    }

    /**
     * Returns the PunishmentType from the string stored in the database
     *
     * @return the PunishmentType of this punishment instance
     */
    public PunishmentType getPunishmentType() {
        return PunishmentType.valueOf(type);
    }
}
