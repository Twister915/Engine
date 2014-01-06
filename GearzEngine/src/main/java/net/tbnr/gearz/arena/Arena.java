package net.tbnr.gearz.arena;


import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.GearzException;
import net.tbnr.gearz.game.voting.Votable;
import net.tbnr.util.RandomUtils;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Arena class. Also can be voted upon.
 */
@EqualsAndHashCode(of = {"id", "gameId", "worldId"}, doNotUseGetters = true)
@ToString(of = {"name", "authors", "description", "worldId"})
public abstract class Arena implements Votable {
    @Getter @Setter
    private String gameId;
    @Getter
    private String name;
    @Getter
    private String authors;
    @Getter
    private String description;
    @Getter
    private String worldId;
    @Getter
    private String id;

    @Getter
    private World world = null;

    private static GridFS bucket = null;

    static {
        if (Arena.bucket == null) {
            bucket = new GridFS(Gearz.getInstance().getMongoDB(), "worlds");
        }
    }

    public Arena(String name, String author, String description, String worldId, String id) {
        this.name = name;
        this.authors = author;
        this.description = description;
        this.worldId = worldId;
        this.id = id;
    }

    public Arena(String name, String author, String description, World world) {
        this.name = name;
        this.authors = author;
        this.description = description;
        this.world = world;
    }

    public void loadWorld() throws GearzException, ZipException, IOException {
        GridFSDBFile one = Arena.bucket.findOne(new ObjectId(this.worldId));
        if (one == null) {
            throw new GearzException("Failed to load world - not found");
        }
        String worldName = RandomUtils.getRandomString(16);
        File zipHandle = new File(Gearz.getInstance().getDataFolder(), RandomUtils.getRandomString(16) + ".gWorld");
        one.writeTo(zipHandle);
        File world = new File(Bukkit.getWorldContainer(), worldName);
        if (!world.mkdir()) {
            throw new GearzException("Could not create world directory!");
        }
        ZipFile zippedWorld = new ZipFile(zipHandle);
        zippedWorld.extractAll(world.getPath());
        this.world = WorldCreator.name(worldName).createWorld();
        this.world.setTime(0);
        this.world.setStorm(false);
        this.world.setAutoSave(false);
        for (LivingEntity e : this.world.getLivingEntities()) {
            if (e instanceof Player) {
                continue;
            }
            e.remove();
        }
        for (Item e : this.world.getEntitiesByClass(Item.class)) {
            e.remove();
        }
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setGameRuleValue("doMobLoot", "true");
        this.world.setGameRuleValue("commandBlockOutput", "true");
        this.world.setGameRuleValue("doMobSpawning", "true");
        this.world.setGameRuleValue("keepInventory", "false");
    }

    public void saveWorld() throws ZipException, IOException, GearzException {
        this.world.save();
        File worldFolder = this.world.getWorldFolder();
        String randomString = RandomUtils.getRandomString(16);
        File gearzZipFile = new File(Gearz.getInstance().getDataFolder(), randomString + ".gZip");
        ZipFile zipFile = new ZipFile(gearzZipFile);
        File[] files = worldFolder.listFiles();
        if (!worldFolder.isDirectory() || files == null) {
            throw new GearzException("World director is not a world!");
        }
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        for (File file : files) {
            Gearz.getInstance().getLogger().info("Adding " + file.getName());
            if (file.isDirectory()) {
                zipFile.addFolder(file, zipParameters);
            } else {
                zipFile.addFile(file, zipParameters);
            }
        }
        GridFSInputFile file = Arena.bucket.createFile(gearzZipFile);
        file.setFilename(randomString);
        file.setContentType("application/zip");
        file.save();
        gearzZipFile.delete();
        this.worldId = file.getId().toString();
    }

    public void unloadWorld() {
        File worldFolder = this.world.getWorldFolder();
        Bukkit.unloadWorld(this.world, false);
        delete(worldFolder);
    }

    public static boolean delete(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                if (!delete(new File(dir, aChildren))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public Location pointToLocation(Point p) {
        return new Location(this.getWorld(), p.getX(), p.getY(), p.getZ(), p.getPitch(), p.getYaw());
    }

    public static Point pointFromLocation(Location l) {
        return new Point(l.getX(), l.getY(), l.getZ(), l.getPitch(), l.getYaw());
    }

    public void cleanupDrops() {
        cleanupEntities(Item.class);
    }

    @SafeVarargs
    public final void cleanupEntities(Class<? extends Entity>... entities) {
        for (Entity entity : this.world.getEntitiesByClasses(entities)) {
            entity.remove();
        }
    }

    @SafeVarargs
    public final void cleanupAllEntitiesExcept(Class<? extends Entity>... entities) {
        List<Class<? extends Entity>> classes = Arrays.asList(entities);
        for (Entity entity : this.world.getEntities()) {
            if (classes.contains(entity.getClass())) {
                continue;
            }
            entity.remove();
        }

    }
}
