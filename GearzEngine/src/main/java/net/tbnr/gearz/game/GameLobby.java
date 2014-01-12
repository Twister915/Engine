package net.tbnr.gearz.game;


import net.tbnr.gearz.arena.*;
import org.bukkit.World;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/25/13
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
@ArenaCollection(collection = "game_lobbys_v2")
@ArenaMeta(meta = {"game:%key"})
public final class GameLobby extends Arena {

    @ArenaField(loop = true, longName = "Spawn Points", key = "spawn_points", type = ArenaField.PointType.Player)
    public ArenaIterator<Point> spawnPoints;

    public GameLobby(String name, String author, String description, String worldId, String id) {
        super(name, author, description, worldId, id);
    }

    public GameLobby(String name, String author, String description, World world) {
        super(name, author, description, world);
    }
}
