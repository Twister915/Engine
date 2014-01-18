package net.tbnr.gearz;

import com.mongodb.DBObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import net.lingala.zip4j.exception.ZipException;
import net.tbnr.gearz.arena.*;
import net.tbnr.gearz.game.GameLobby;
import net.tbnr.gearz.game.GameMeta;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import net.tbnr.util.player.TPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * ArenaSetup class, used to represent a setup session.
 */
public class ArenaSetup implements Listener, TCommandHandler, SkullDelegate {
    private ArenaSetupStage state;

    public GameMeta getMeta() {
        return meta;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GameSetupFactory.handleCommandStatus(status, sender);
    }

    @Override
    public void onComplete(SkullTask task) {
        this.waitingForAsync = false;
        List<Block> blocksFound = task.getBlocksFound();
        for (Block block : blocksFound) {
            blocksToReplace.add(new ReplacementBlock(block.getLocation(), ((Skull) block.getState()).getSkullType()));
            if (!block.getWorld().equals(this.world)) continue;
            this.points.add(Arena.pointFromLocation(block.getLocation()));
            block.setType(Material.AIR);
        }
        this.player.sendMessage(GearzSetup.getInstance().getFormat("formats.blocks-added", true, new String[]{"<num>", String.valueOf(blocksFound.size())}));
    }

    @Override
    public void locatedBlock(Block block) {

    }

    private static enum ArenaSetupStage {
        Name,
        Author,
        Description,
        Selection,
        Fields,
        Completed
    }

    private Class<? extends Arena> arena;
    private GameMeta meta;
    private TPlayer player;
    private String name;
    private String author;
    private String description;
    private Location l1 = null;
    private Location l2 = null;
    private HashMap<ArenaField, ArenaIterator> fields;
    private ArenaField fieldIndex;
    private ArenaManager manager;
    private Iterator<ArenaField> iterator;
    private ArenaIterator<Point> points;
    private boolean complete = false;
    private ArrayList<ReplacementBlock> blocksToReplace = new ArrayList<>();
    private boolean waitingForAsync = false;
    private World world;

    public ArenaSetup(ArenaManager manager, Class<? extends Arena> arena, GameMeta meta, TPlayer player) {
        this.manager = manager;
        this.player = player;
        this.world = player.getPlayer().getWorld();
        this.meta = meta;
        this.arena = arena;
        this.fields = new HashMap<>();
        for (Field field : this.arena.getFields()) {
            if (!(field.isAnnotationPresent(ArenaField.class))) continue;
            if (!field.isAnnotationPresent(ArenaField.class)) continue;
            GearzSetup.getInstance().getLogger().info("Field detected " + field.getName());
            ArenaField annotation = field.getAnnotation(ArenaField.class);
            this.fields.put(annotation, null);
        }
        this.iterator = this.fields.keySet().iterator();
    }

    public void startSetup() {
        GearzSetup.getInstance().registerEvents(this);
        GearzSetup.getInstance().registerCommands(this);
        this.player.clearInventory();
        this.player.getPlayer().setGameMode(GameMode.CREATIVE);
        this.player.giveItem(Material.DIAMOND_HOE, 1, (short) 0, GearzSetup.getInstance().getFormat("formats.dhoe"), new String[]{"Left or Right click to select a point"});
        this.player.giveItem(Material.DIAMOND_AXE, 1, (short) 0, GearzSetup.getInstance().getFormat("formats.daxe"), new String[]{"Left click to select point one.", "Right click to set a standing point"});
        this.player.giveItem(Material.GOLD_SPADE, 1, (short) 0, GearzSetup.getInstance().getFormat("formats.gshovel"), new String[]{"Click to finish setting up this field."});
        this.state = ArenaSetupStage.Name;
        moveOn();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(this.player.getPlayer())) return;
        switch (this.state) {
            case Name:
                this.name = event.getMessage();
                this.state = ArenaSetupStage.Author;
                break;
            case Author:
                this.author = event.getMessage();
                this.state = ArenaSetupStage.Description;
                break;
            case Description:
                this.description = event.getMessage();
                this.state = ArenaSetupStage.Selection;
                break;
            default:
                return;
        }
        event.setCancelled(true);
        moveOn();
    }

    private void moveOn() {
        switch (this.state) {
            case Name:
                this.player.sendMessage(GearzSetup.getInstance().getFormat("formats.name-prompt"));
                break;
            case Author:
                this.player.sendMessage(GearzSetup.getInstance().getFormat("formats.author-prompt"));
                break;
            case Description:
                this.player.sendMessage(GearzSetup.getInstance().getFormat("formats.description-prompt"));
                break;
            case Selection:
                this.player.sendMessage(GearzSetup.getInstance().getFormat("formats.select-prompt"));
                break;
            case Fields:
                this.player.sendMessage(GearzSetup.getInstance().getFormat("formats.field-prompt", false, new String[]{"<field>", this.fieldIndex.longName()}));
                break;
            case Completed:
                this.player.sendMessage(GearzSetup.getInstance().getFormat("formats.completed"));
                break;
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().equals(this.player.getPlayer())) return;
        if (event.getAction() == Action.PHYSICAL) return;
        Material type = event.getPlayer().getItemInHand().getType();
        switch (type) {
            case DIAMOND_AXE:
                if (this.state != ArenaSetupStage.Selection) return;
                if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                    return;
                regionSelection(event.getClickedBlock().getLocation(), event.getAction());
                event.setCancelled(true);
                event.getPlayer().sendMessage(GearzSetup.getInstance().getFormat("formats.selected", false, new String[]{"<count>", event.getAction() == Action.RIGHT_CLICK_BLOCK ? "2" : "1"}));
                break;
            case DIAMOND_HOE:
                if (this.state != ArenaSetupStage.Fields) return;
                if (this.points == null) return;
                Point relative = Arena.pointFromLocation((this.fieldIndex.type() == ArenaField.PointType.Block ? event.getClickedBlock().getLocation() : event.getPlayer().getLocation()));
                if (this.points.contains(relative)) {
                    event.getPlayer().sendMessage(GearzSetup.getInstance().getFormat("formats.already-exists"));
                }
                if (!this.world.equals(event.getPlayer().getWorld())) return;
                this.points.add(relative);
                event.getPlayer().sendMessage(GearzSetup.getInstance().getFormat("formats.selected", false, new String[]{"<count>", String.valueOf(this.points.getArrayList().size())}));
                break;
            case GOLD_SPADE:
                //completion
                completePhase();
                break;
        }
    }

    private void regionSelection(Location point, Action action) {
        if (action == Action.RIGHT_CLICK_BLOCK) {
            this.l2 = point;
        }
        if (action == Action.LEFT_CLICK_BLOCK) {
            this.l1 = point;
        }
    }

    private void completePhase() {
        switch (this.state) {
            case Selection:
                if (this.l1 == null || this.l2 == null) {
                    this.player.sendMessage(GearzSetup.getInstance().getFormat("foramts.complete-fail"));
                    return;
                }
                this.state = ArenaSetupStage.Fields;
                completePhase();
                break;
            case Fields:
                if (this.waitingForAsync) {
                    return;
                }
                if (this.points != null) {
                    this.fields.put(this.fieldIndex, this.points);
                }
                if (!this.iterator.hasNext()) {
                    this.state = ArenaSetupStage.Completed;
                } else {
                    this.fieldIndex = this.iterator.next();
                    try {
                        this.points = new ArenaIterator<>();
                    } catch (GearzException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                break;
        }
        moveOn();
    }

    private boolean commitArena() {
        Arena arena;
        try {
            arena = this.arena.getConstructor(String.class, String.class, String.class, World.class).newInstance(this.name, this.author, this.description, this.world);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        for (Field field : arena.getClass().getFields()) {
            if (!field.isAnnotationPresent(ArenaField.class)) continue;
            if (!field.getType().equals(ArenaIterator.class)) continue;
            ArenaIterator arenaIterator = this.fields.get(field.getAnnotation(ArenaField.class));
            try {
                field.set(arena, arenaIterator);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            arena.saveWorld();
        } catch (ZipException | IOException | GearzException e) {
            e.printStackTrace();
            return false;
        }
        /*if (this.arena.equals(GameLobby.class)) {
            DBObject object = ArenaManager.objectFromArena(arena);
            object.put("game", this.meta.key());
            GearzSetup.getInstance().getMongoDB().getCollection("game_lobbys_v2").insert(object);
            return true;
        }*/
        ArenaCollection collection = this.arena.getAnnotation(ArenaCollection.class);
        if (collection != null) {
            DBObject object = ArenaManager.objectFromArena(arena);
            ArenaMeta meta = this.arena.getAnnotation(ArenaMeta.class);
            if (meta != null) {
                for (String s : meta.meta()) {
                    String[] split = s.split(":");
                    if (split.length != 2) continue;
                    object.put(split[0], split[1].replaceAll("%key", this.meta == null ? "" : this.meta.key()));
                }
            }
            GearzSetup.getInstance().getMongoDB().getCollection(collection.collection()).insert(object);
            return true;
        }
        try {
            this.manager.addArena(arena);
        } catch (GearzException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
    Need a /clear <phase> command (redos)
    Need a /back <phase> command (edits)
     */
    @TCommand(
            permission = "gearzsetup.use",
            usage = "/done",
            senders = {TCommandSender.Player},
            name = "done")
    @SuppressWarnings("unused")
    public TCommandStatus done(CommandSender sender, TCommandSender type, TCommand meta, org.bukkit.command.Command command, String[] args) {
        if (this.state != ArenaSetupStage.Completed || !sender.equals(this.player.getPlayer()) || complete) {
            sender.sendMessage(GearzSetup.getInstance().getFormat("formats.cannot-complete"));
            return TCommandStatus.SUCCESSFUL;
        }
        if (commitArena()) {
            for (ReplacementBlock block : this.blocksToReplace) {
                block.place();
            }
            sender.sendMessage(GearzSetup.getInstance().getFormat("formats.saved"));
            this.player.resetPlayer();
            this.player.playSound(Sound.LEVEL_UP);
            HandlerList.unregisterAll(this);
            complete = true;
        } else {
            sender.sendMessage(GearzSetup.getInstance().getFormat("formats.failed-save"));
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            permission = "gearzsetup.use",
            usage = "/skullkpoints <skull_type>",
            senders = {TCommandSender.Player},
            name = "skullpoints")
    public TCommandStatus blockPoints(final CommandSender sender, TCommandSender type, TCommand meta, org.bukkit.command.Command command, String[] args) {
        final SkullType skullType;
        switch (args[0]) {
            case "creeper":
                skullType = SkullType.CREEPER;
                break;
            case "wither":
                skullType = SkullType.WITHER;
                break;
            case "skeleton":
                skullType = SkullType.SKELETON;
                break;
            case "zombie":
                skullType = SkullType.ZOMBIE;
                break;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
        //final List<Block> blocks = new ArrayList<>();
        this.waitingForAsync = true;
        SkullTask task = new SkullTask(player.getPlayer().getWorld(), skullType, 240, LocationUtil.getMinimum(l1, l2), LocationUtil.getMaximum(l1, l2), this);
        task.setup();
        /*
        blocks.add(t);
                                blocksToReplace.add(new ReplacementBlock(t.getLocation(), state1.getSkullType()));
        for (Block b : blocks) {
            this.points.add(this.schematic.getRelative(b.getLocation()));
            b.setType(Material.AIR);
        }
        this.player.sendMessage(GearzSetup.getInstance().getFormat("formats.blocks-added", true, new String[]{"<num>", String.valueOf(blocks.size())}));*/

        return TCommandStatus.SUCCESSFUL;
    }


    @Data
    @AllArgsConstructor
    @ToString
    public static class ReplacementBlock {
        private Location blockLocation;
        private SkullType skullType;

        public void place() {
            /*Block block = blockLocation.getBlock();
            block.setType(Material.SKULL);
            Skull block1 = (Skull) block;
            block1.setSkullType(skullType);*/
        }
    }
}
