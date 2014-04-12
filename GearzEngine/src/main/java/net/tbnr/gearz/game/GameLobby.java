/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

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
    public PointIterator spawnPoints;

    public GameLobby(String name, String author, String description, String worldId, String id) {
        super(name, author, description, worldId, id);
    }

    public GameLobby(String name, String author, String description, World world) {
        super(name, author, description, world);
    }
}
