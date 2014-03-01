package net.tbnr.gearz.votifier;

import net.tbnr.gearz.player.GearzPlayer;

import java.util.Date;

public interface VotifierListener {
    public void onVote(GearzPlayer player, String site, Date time);
}
