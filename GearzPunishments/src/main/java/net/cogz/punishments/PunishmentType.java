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

package net.cogz.punishments;

import lombok.Getter;

/**
 * A Enum for the types of punishments
 * and their actions, and whether or not
 * they warrant kicks when punished with
 */
public enum PunishmentType {
    PERMANENT_BAN("banned", true),
    TEMP_BAN("temp banned", true),
    MUTE("muted"),
    TEMP_MUTE("temp muted"),
    WARN("warned"),
    IP_BAN("ip banned"),
    KICK("kicked", true);

    /**
     * The action used in the punishment broadcast
     */
    @Getter
    private String action = "invalid";

    /**
     * Whether or not this PunishmentType
     * warrants a player to be kicked
     */
    @Getter
    private boolean kickable = false;

    /**
     * Punishment that does not kick a player
     *
     * @param action action used in the punishment broadcast
     */
    PunishmentType(String action) {
        this.action = action;
    }

    /**
     * A punishment that will kick a player
     *
     * @param action action used in the punishment broadcast
     * @param kickable whether or not a punishment with this type warrants a kick
     */
    PunishmentType(String action, boolean kickable) {
        this.action = action;
        this.kickable = kickable;
    }
}
