package net.tbnr.util;

import lombok.Getter;
import lombok.Setter;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.server.Server;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jake on 12/27/13.
 */
public class ServerSelector implements Listener {
    /**
     * And ArrayList of all the items displayed on the GUI
     */
    @Getter
    private ArrayList<InventoryGUI.InventoryGUIItem> items;
    /**
     * The title of the GUI
     */
    @Getter
    private String title;
    /**
     * The callback of the GUI
     */
    @Getter
    private SelectorCallback callback;

    /**
     * The inventory which is used to display the GUI
     */
    @Getter
    private Inventory inventory;

    @Getter String gameType;

    @Getter @Setter List<Server> servers;

    public ServerSelector(String gameType, SelectorCallback selectorCallback) {
        Bukkit.getServer().getPluginManager().registerEvents(this, Gearz.getInstance());
        this.gameType = gameType;
        this.callback = selectorCallback;
        this.title = gameType + " Servers";
        this.servers = InventoryRefresher.getServersForSelector(this);
        this.items = getServerItems();
        this.inventory = Bukkit.createInventory(null, determineSize(), title);
        update();
    }

    public void open(Player player) {
        player.openInventory(inventory);
        getCallback().onSelectorOpen(this, player);
        Gearz.getInstance().getInventoryRefresher().add(this);
    }

    public void close(Player player) {
        player.closeInventory();
        closeCallback(player);
    }

    private void closeCallback(Player player) {
        getCallback().onSelectorClose(this, player);
        Gearz.getInstance().getInventoryRefresher().remove(this);
    }

    public void update() {
        inventory.clear();
        if (items == null) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            InventoryGUI.InventoryGUIItem item = items.get(i);
            if (item == null) {
                continue;
            }
            item.setSlot(i);
            inventory.setItem(i, item.getItem());
        }
    }

    private int determineSize() {
        /*if (items == null) return 9;
        if (items.size() <= 9) return 9;
        else if (items.size() <= 18) return 18;
        else if (items.size() <= 27) return 27;
        else return 36;*/
        int rowSize = 9;
        if (items == null || items.size() == 0) {
            return rowSize;
        }
        float i = items.size() % rowSize; //Remainder of items over 9
        float v = i / rowSize; //Convert to a decimal
        return (int) (Math.floor(items.size() / rowSize) /* Gives how many rows we need */ + Math.ceil(v) /* If we need an extra row */) * rowSize /*Times number of items per row */;
    }

    private ArrayList<InventoryGUI.InventoryGUIItem> getServerItems() {
        ArrayList<InventoryGUI.InventoryGUIItem> items = new ArrayList<>();
        for (Server aServer : servers) {
            items.add(itemForServer(aServer));
        }
        return items;
    }

    public static InventoryGUI.InventoryGUIItem itemForServer(Server server) {
        DyeColor color = null;

        String status = server.getStatusString();
        switch (status) {
            case "lobby":
                color = DyeColor.LIME;
                break;
            case "spectate":
                color = DyeColor.YELLOW;
                break;
            case "load_lobby":
            case "load-map":
            case "game-over":
                color = DyeColor.RED;
                break;
        }

        Wool wool = new Wool(color);
        ItemStack itemStack = wool.toItemStack(1);

        String serverName = Gearz.getInstance().getFormat("formats.blastoff-itemname", true, new String[]{"<game>", server.getGame()}, new String[]{"<number>", server.getNumber().toString()});

        itemStack.setAmount(server.getPlayerCount() == null ? 1 : Math.max(1, server.getPlayerCount()));
        return new InventoryGUI.InventoryGUIItem(itemStack, serverName);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        if (event.getInventory().getType() == null) {
            return;
        }
        if (this.inventory == null) {
            return;
        }
        if (!(event.getInventory().getTitle().equalsIgnoreCase(this.inventory.getTitle()))) {
            return;
        }
        Player player = (Player) event.getPlayer();
        closeCallback(player);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (this.inventory == null) {
            return;
        }
        if (!(event.getInventory().getTitle().equalsIgnoreCase(this.inventory.getTitle()))) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        boolean cont = false;
        switch (event.getClick()) {
            case RIGHT:
            case LEFT:
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
            case MIDDLE:
            case NUMBER_KEY:
            case DROP:
                cont = true;
                break;
        }
        if (!cont) {
            return;
        }
        for (InventoryGUI.InventoryGUIItem item : this.items) {
            if (item == null) {
                continue;
            }
            if (event.getCurrentItem() == null) {
                continue;
            }
            if (!(event.getCurrentItem().equals(item.getItem()))) {
                continue;
            }
            this.callback.onItemSelect(this, item, player);
        }
        event.setCancelled(true);
    }

    public interface SelectorCallback {
        /**
         * Called when something has been pressed in the inventory GUI
         *
         * @param item   is the item that was pressed
         * @param player is the player who pressed it
         */
        public void onItemSelect(ServerSelector selector, InventoryGUI.InventoryGUIItem item, Player player);

        /**
         * Called when the inventory is opened
         *
         * @param selector is the gui that was opened
         * @param player   is the player for whi the GUI was opened
         */
        public void onSelectorOpen(ServerSelector selector, Player player);

        /**
         * Called when the inventory is closed
         *
         * @param selector is the gui that was closed
         * @param player   is the player for who the GUI was closed
         */
        public void onSelectorClose(ServerSelector selector, Player player);
    }
}
