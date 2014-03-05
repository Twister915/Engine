package net.cogz.punishments;

import com.mongodb.DB;
import com.mongodb.DBObject;
import lombok.ToString;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;

import java.util.Date;

/**
 * Created by jake on 3/4/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@ToString
public class Punishment extends GModel {
    public @BasicField String issuer;
    public @BasicField String punished;
    public @BasicField String reason;
    public @BasicField Boolean valid;
    public @BasicField String type;
    public @BasicField Date time;
    public @BasicField Date end;

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

    public Punishment(DB database, String punished) {
        this(database);
        this.punished = punished;
    }

    public PunishmentType getPunishmentType() {
        return PunishmentType.valueOf(type);
    }
}
