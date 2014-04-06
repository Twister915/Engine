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

package net.tbnr.gearz.arena;

import net.tbnr.gearz.GearzException;

import java.util.List;

public final class RegionIterator extends ArenaIterator<Region> {
    public RegionIterator(List<Region> stuff) throws GearzException {
        super(stuff);
    }

    public RegionIterator() throws GearzException {
    }
}
