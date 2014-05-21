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

package net.cogzmc.engine.gearz.game;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/10/13
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface GameCountdownHandler {
    public void onCountdownStart(Integer max, GameCountdown countdown);

    public void onCountdownChange(Integer seconds, Integer max, GameCountdown countdown);

    public void onCountdownComplete(GameCountdown countdown);
}
