package net.cogz.gearz.hub.modules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cogz.gearz.hub.GearzHub;
import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import net.tbnr.gearz.netcommand.BouncyUtils;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.ServerSelector;
import net.tbnr.util.inventory.InventoryGUI;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jake on 12/27/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@HubModuleMeta(
        key = "blastoff"
)
public class BlastOffSigns extends HubModule implements Listener {
    final private Map<TPlayer, SignData> inUse = new HashMap<>();
    Integer distance;

    public BlastOffSigns() {
        super(false, true);
        distance = (Integer) getPropertyObject("distance");
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN) return;
        Sign sign = (Sign) block.getState();
        final String[] lines = sign.getLines();
        if (lines.length != 2 || ServerManager.getServersWithGame(lines[1]).size() == 0 || !lines[0].equals(GearzHub.getInstance().getFormat("formats.blastoff-topline", true))) return;
        final ServerSelector serverSelector = new ServerSelector(lines[1], new ServerSelector.SelectorCallback() {
            @Override
            public void onItemSelect(ServerSelector selector, InventoryGUI.InventoryGUIItem item, Player player) {

                /**
                 * The reason you need to test as the person could have the selector
                 * open for a while, and he clicks the last item while a server is restarting
                 * so the server is no longer online and therefore is not in the servers list
                 * Though the inventory is already open so it's not updated
                 * Therefore it causes and IndexOutOfBoundsException
                 * @see java.lang.IndexOutOfBoundsException
                 */
                Server server = selector.getServers().get(
                        /** if */ item.getSlot() > selector.getServers().size() ?
                        /** true */ 0 : /** false */ item.getSlot()
                );

                if (server.isCanJoin()) {
                    selector.close(player);
                    SignData signData = new SignData(server, player.getLocation().getBlockY());
                    inUse.put(TPlayerManager.getInstance().getPlayer(player), signData);
                    player.setVelocity(new Vector(0, 4, 0));
                }
            }

            @Override
            public void onSelectorOpen(ServerSelector selector, Player player) {
            }

            @Override
            public void onSelectorClose(ServerSelector selector, Player player) {
            }
        });
        serverSelector.open(event.getPlayer());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        TPlayer tPlayer = TPlayerManager.getInstance().getPlayer(player);
        if (!inUse.containsKey(tPlayer)) return;
        Integer y = event.getTo().getBlockY();
        SignData signData = inUse.get(tPlayer);
        /**
         * This handles if a player hit a block before reaching the distance
         */
        if (event.getTo().getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
            BouncyUtils.sendPlayerToServer(player, signData.getServer().getBungee_name());
            inUse.remove(tPlayer);
            return;
        }
        if (y - signData.getStart() >= distance) {
            BouncyUtils.sendPlayerToServer(player, signData.getServer().getBungee_name());
            inUse.remove(tPlayer);
        }

    }

    @AllArgsConstructor
    @Data
    @EqualsAndHashCode
    public class SignData {
        private Server server;
        private Integer start;
    }
}