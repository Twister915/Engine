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

package net.tbnr.gearz.player.bungee;

import java.util.List;

/**
 * Delegate for GearzPermissions which
 * allows for the retrieval of player
 * metadata including prefixes, suffixes,
 * and other things required for permissions
 *
 * <p>
 * Latest Change: Create
 * <p>
 *
 * @author Jake
 * @since 2/12/2014
 */
public interface PermissionsDelegate {
    String getPrefix(String player);

    String getSuffix(String player);

    String getTabColor(String player);

    String getNameColor(String player);

    List<String> getAllPermissions(String player);
}
