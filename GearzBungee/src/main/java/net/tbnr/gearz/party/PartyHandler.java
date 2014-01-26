package net.tbnr.gearz.party;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 1/26/14.
 */
public class PartyHandler implements Listener {
    @Getter public List<Party> parties;

    public PartyHandler() {
        this.parties = new ArrayList<>();
    }

    public Party getPartyFromPlayer(ProxiedPlayer player) {
        for (Party party : parties) {
            if (party.getCreator() == player) return party;
            for (ProxiedPlayer member : party.getMembers()) {
                if (member == player) return party;
            }
        }
        return null;
    }

    public boolean hasInviteFor(ProxiedPlayer player, Party party) {
        List<Party> invites = getPendingInvites(player);
        return invites.contains(party);
    }

    private List<Party> getPendingInvites(ProxiedPlayer player) {
        List<Party> invites = new ArrayList<>();
        for (Party party : this.parties) {
            for (ProxiedPlayer invitee : party.getPendingInvites()) {
                if (invitee == player) invites.add(party);
            }
        }
        return invites;
    }

    public Party create(ProxiedPlayer creator) {
        if (getPartyFromPlayer(creator) != null) {
            creator.sendMessage(GearzBungee.getInstance().getFormat("party-already-have"));
            return null;
        }
        return new Party(creator);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onServerConnect(ServerConnectedEvent event) {
        Party party = getPartyFromPlayer(event.getPlayer());
        if (party == null) return;
        if (party.getCreator() != event.getPlayer()) return;
        party.connect(event.getServer().getInfo());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        Party party = getPartyFromPlayer(event.getPlayer());
        if (party == null) return;
        if (party.getCreator() == event.getPlayer()) {
            GearzBungee.getInstance().getFormat("party-disconnected-leader", false, false, new String[]{"<leader>", event.getPlayer().getName()});
            party.disband();
            return;
        }
        party.sendMessage(GearzBungee.getInstance().getFormat("party-disconnect", false, false, new String[]{"<player>", event.getPlayer().getName()}));
        party.getMembers().remove(event.getPlayer());
    }
}
