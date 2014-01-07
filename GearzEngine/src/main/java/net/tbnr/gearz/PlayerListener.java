package net.tbnr.gearz;

import net.tbnr.gearz.netcommand.NetCommand;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.player.TPlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/31/13
 * Time: 11:29 PM
 */
public class PlayerListener implements Listener {



    @EventHandler
    @SuppressWarnings("unused")
    public void tPlayerJoinEvent(TPlayerJoinEvent event) {
        NetCommand.withName("update_p").withArg("name", event.getPlayer());
    }

    @EventHandler
    public void playerJoinEvent(PlayerLoginEvent event) {
        //If People can join the server then just move on if not kick them WHITELIST STUFF
        if (ServerManager.canJoin()) return;
        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        event.setKickMessage("You are not permitted to join this server at this time.");
    }
}
