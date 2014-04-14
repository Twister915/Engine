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

package net.tbnr.gearz.server;

/**
 * Allows for management of the {@link Server}
 * that the {@link ServerManager} is currently
 * running on by providing information
 * such as the game and name of the bungee server.
 * This class is implemented in a main plugin
 * for management of the current {@link Server}.
 *
 * <p>
 * Latest Change: Created
 * <p>
 *
 * @author Joey
 * @since 12/17/2013
 */
public interface ServerManagerHelper {
    public String getBungeeName();

    public String getGame();

    public boolean isGameServer();
}
