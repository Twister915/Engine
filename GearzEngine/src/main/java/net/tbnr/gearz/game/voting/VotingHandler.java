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
