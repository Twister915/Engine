package net.cogz.gearz.hub.modules;

import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.GearzHub;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by rigor789 on 2014.01.10..
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
@HubModuleMeta(
        key = "signedit"
)
public class SignEdit extends HubModule implements Listener, TCommandHandler {

    private final HashMap<String, Sign> players;
    private final String name;

    public SignEdit() {
        super(true, true);
        this.players = new HashMap<>();
        this.name = ChatColor.AQUA + "The magic SIGN!!!!!";
    }

    @EventHandler
    public void onSignPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getPlayer().getItemInHand() == null) return;
        if (event.getPlayer().getItemInHand().getType() != Material.SIGN) return;
        if (!event.getPlayer().getItemInHand().hasItemMeta()) return;
        if (!event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(this.name)) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) event.getClickedBlock().getState();
        Sign gui = (Sign) event.getClickedBlock().getState();
        for (int i = 0; i <= sign.getLines().length - 1; i++) {
            GearzHub.getInstance().getLogger().info("SignEdit >>>> THE LINE is: " + sign.getLine(i));
            if (sign.getLine(i) == null) continue;
            gui.setLine(i, sign.getLine(i));
        }
        this.players.put(event.getPlayer().getName(), sign);
        event.getPlayer().sendMessage(ChatColor.AQUA + "SignEdit session started. you should be able to edit the sign!");
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        if (!this.players.containsKey(event.getPlayer().getName())) return;
        Sign sign = players.get(event.getPlayer().getName());
        for (int i = 0; i <= event.getLines().length - 1; i++) {
            GearzHub.getInstance().getLogger().info("SignEdit >>>> Line is: " + event.getLine(i));
            if (event.getLine(i) == null) continue;
            sign.setLine(i, event.getLine(i));
        }
        event.getBlock().setType(Material.AIR);
        event.setCancelled(true);
        this.players.remove(event.getPlayer().getName());
    }

    private class SignUpdater extends BukkitRunnable {

        private final Sign sign;
        private final Sign gui;
        private String[] lines;

        public SignUpdater(Sign sign, Sign gui) {
            this.sign = sign;
            this.gui = gui;
        }

        public void setLines(String[] lines) {
            this.lines = lines;
        }

        @Override
        public void run() {
            for (int i = 0; i < 4; i++) {
                if (lines[i].isEmpty()) continue;
                this.sign.setLine(i, lines[i]);
            }
        }
    }

    @TCommand(
            name = "magicsign",
            usage = "/magicsign",
            permission = "gearz.magicsign",
            senders = {TCommandSender.Player}
    )
    public TCommandStatus magicsign(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        Player player = (Player) sender;
        ItemStack magic = new ItemStack(Material.SIGN);
        ItemMeta itemMeta = magic.getItemMeta();
        itemMeta.setDisplayName(name);
        magic.setItemMeta(itemMeta);
        player.getInventory().addItem(magic);
        sender.sendMessage(ChatColor.GREEN + "Sign Given!");
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzHub.handleCommandStatus(status, sender);
    }
}
