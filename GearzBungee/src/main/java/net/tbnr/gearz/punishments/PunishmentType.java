package net.tbnr.gearz.punishments;

/**
 * Created by jake on 1/4/14.
 */
public enum PunishmentType {
    PERMANENT_BAN,
    TEMP_BAN,
    MUTE,
    TEMP_MUTE,
    WARN,
    IP_BAN,
    KICK;

    public String getAction() {
        if (this.equals(PERMANENT_BAN)) return "banned";
        else if (this.equals(TEMP_BAN)) return "temp banned";
        else if (this.equals(MUTE)) return "muted";
        else if (this.equals(TEMP_MUTE)) return "temp muted";
        else if (this.equals(WARN)) return "warned";
        else if (this.equals(IP_BAN)) return "ip banned";
        else if (this.equals(KICK)) return "kicked";
        return "invalid";
    }

    public boolean isKickable() {
        switch (this) {
            case PERMANENT_BAN:
            case IP_BAN:
            case TEMP_BAN:
            case KICK:
                return true;
            default:
                return false;
        }
    }
}
