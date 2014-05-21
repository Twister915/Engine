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

package net.cogzmc.engine.util.delegates;

import java.util.List;

/**
 * Created by jake on 2/12/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public interface PermissionsDelegate {
    String getPrefix(String player);

    String getSuffix(String player);

    String getTabColor(String player);

    String getNameColor(String player);

    List<String> getAllPermissions(String player);
}
