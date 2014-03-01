package net.tbnr.gearz.votifier;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.GearzException;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public final class VotifierMaster implements Listener {
    public VotifierMaster() {
        Bukkit.getScheduler().runTaskLater(Gearz.getInstance(), new Runnable() {
            @Override
            public void run() {
                checkPendingVotes();
            }
        }, 600L);
    }

    private final List<VotifierListener> listeners = new LinkedList<>();
    private final List<Vote> pendingVotes = new LinkedList<>();

    @EventHandler
    public void onVote(VotifierEvent event) {
        if (this.listeners.size() == 0) return;
        Vote vote = event.getVote();
        Player p;
        try {
            p = resolvePlayer(vote);
        } catch (GearzException e) {
            makeVotePending(vote);
            return;
        }
        sendVote(p, vote);
    }

    private Player resolvePlayer(Vote vote) throws GearzException {
        Player p = Bukkit.getPlayer(vote.getUsername());
        if (p == null) {
            throw new GearzException("Could not find player");
        }
        return p;
    }

    private void sendVote(Player p, Vote vote) {
        String site = vote.getServiceName();
        Date timestamp = new Date((long)Integer.valueOf(vote.getTimeStamp())*1000);
        GearzPlayer player = GearzPlayer.playerFromPlayer(p);
        for (VotifierListener listener : listeners) {
            listener.onVote(player, site, timestamp);
        }
        Gearz.getInstance().getLogger().info("Dispatched vote to Votifier listeners (Gearz level) " + vote.getUsername() + " on " + vote.getServiceName() + " via " + vote.getAddress());
    }
    private void makeVotePending(Vote vote) {
        this.pendingVotes.add(vote);
    }

    public void checkPendingVotes() {
        if (this.listeners.size() == 0 || this.pendingVotes.size() == 0) return;
        for (Vote pendingVote : new LinkedList<>(this.pendingVotes)) {
            Player player;
            try {
                player = resolvePlayer(pendingVote);
            } catch (GearzException e) {
                continue;
            }
            sendVote(player, pendingVote);
            this.pendingVotes.remove(pendingVote);
        }
    }

    public void registerListener(VotifierListener listener) {
        if(!this.listeners.contains(listener))this.listeners.add(listener);
    }

    public void unregisterListener(VotifierListener listener) {
        if (this.listeners.contains(listener))this.listeners.remove(listener);
    }
}
