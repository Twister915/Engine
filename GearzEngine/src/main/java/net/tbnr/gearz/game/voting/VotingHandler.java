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

package net.tbnr.gearz.game.voting;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/12/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VotingHandler {
    public void onVotingDone(Map<Votable, Integer> data, VotingSession session);

}
