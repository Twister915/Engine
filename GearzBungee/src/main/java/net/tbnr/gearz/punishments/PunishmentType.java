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
    PERMANENT_BAN("banned", true),
    TEMP_BAN("temp banned", true),
    MUTE("muted"),
    TEMP_MUTE("temp muted"),
    WARN("warned"),
    IP_BAN("ip banned"),
    KICK("kicked", true);

    @Getter
    private String action = "invalid";

    @Getter
    private boolean kickable = false;

    PunishmentType(String action) {
        this.action = action;
    }

    PunishmentType(String action, boolean kickable) {
        this.action = action;
        this.kickable = kickable;
    }
}
