package net.tbnr.gearz.punishments;

import lombok.Getter;

/**
 * Created by jake on 1/4/14.
 *
 * Purpose Of File: Enum For Punishment's
 *
 * Latest Change: Changed how the action and isKickable was got
 */
public enum PunishmentType {
    PERMANENT_BAN("banned"),
    TEMP_BAN("temp banned"),
    MUTE("muted"),
    TEMP_MUTE("temp muted"),
    WARN("warned"),
    IP_BAN("ip banned"),
    KICK("kicked", true);

    @Getter
    private String action = "invalid";

    private boolean kickable = false;

    PunishmentType(String action) {
        this.action = action;
    }

    PunishmentType(String action, boolean kickable) {
        this.action = action;
        this.kickable = kickable;
    }

    public boolean isKickable() {
        return this.kickable;
    }
}
