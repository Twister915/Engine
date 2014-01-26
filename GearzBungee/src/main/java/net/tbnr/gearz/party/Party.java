package net.tbnr.gearz.party;

import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.modules.PlayerInfoModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 1/26/14.
 */
@ToString
public class Party {
    @Getter public ProxiedPlayer creator;
    @Getter public List<ProxiedPlayer> members;
    @Getter public List<ProxiedPlayer> pendingInvites;

    public Party(ProxiedPlayer creator) {
        this.creator = creator;
        this.members = new ArrayList<>();
    }

    public void disband() {
        for (ProxiedPlayer member : this.members) {
            kick(member, false);
        }
        GearzBungee.getInstance().getPartyHandler().getParties().remove(this);
    }

    public void invite(ProxiedPlayer player) {
        creator.sendMessage(GearzBungee.getInstance().getFormat("party-invite", false, false, new String[]{"<player>", player.getName()}));
        player.sendMessage(GearzBungee.getInstance().getFormat("party-invite-recieve", false, false, new String[]{"<inviter>", this.creator.getName()}));
        this.pendingInvites.add(player);
    }

    public void kick(ProxiedPlayer player, boolean show) {
        members.remove(player);
        if (show) {
            player.sendMessage(GearzBungee.getInstance().getFormat("party-kick-victim"));
        }
    }

    public void leave(ProxiedPlayer player) {
        kick(player, false);
    }

    public void join(ProxiedPlayer player) {
        this.members.add(player);
        this.sendMessage(GearzBungee.getInstance().getFormat("party-message-join", false, false, new String[]{"<player>", player.getName()}));
    }

    public void connect(ServerInfo serverInfo) {
        for (ProxiedPlayer member : this.members) {
            member.sendMessage(GearzBungee.getInstance().getFormat("party-server-join", false, false, new String[]{"<server>", PlayerInfoModule.getServerForBungee(serverInfo).getGame()}));
            member.connect(serverInfo);
        }
    }

    public void sendMessage(String message) {
        String toSend = GearzBungee.getInstance().getFormat("party-message-format", false, false, new String[]{"<message>", message});
        this.creator.sendMessage(toSend);
        for (ProxiedPlayer member : this.members) {
            member.sendMessage(toSend);
        }
    }
}
