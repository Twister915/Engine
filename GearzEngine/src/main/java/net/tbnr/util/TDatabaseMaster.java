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

package net.tbnr.util;

import net.tbnr.util.player.TPlayerManager;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/10/13
 * Time: 10:33 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TDatabaseMaster {
    public TPlayerManager.AuthenticationDetails getAuthDetails();
}
