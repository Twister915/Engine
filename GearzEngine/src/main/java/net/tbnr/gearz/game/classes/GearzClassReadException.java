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

package net.tbnr.gearz.game.classes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.tbnr.gearz.GearzException;
import org.json.JSONException;

@EqualsAndHashCode(callSuper = false)
@Data
public final class GearzClassReadException extends GearzException {
    private JSONException jsonException;

    public GearzClassReadException(String s) {
        super(s);
    }
}
