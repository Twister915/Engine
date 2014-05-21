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

package net.cogzmc.engine.gearz.game.kits;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cogzmc.engine.gearz.GearzException;
import org.json.JSONException;

@EqualsAndHashCode(callSuper = false)
@Data
public final class GearzKitReadException extends GearzException {
    private JSONException jsonException;

    public GearzKitReadException(String s) {
        super(s);
    }
}
