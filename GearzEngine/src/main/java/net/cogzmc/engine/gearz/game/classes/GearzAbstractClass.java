/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.engine.gearz.game.classes;

import lombok.Getter;
import net.cogzmc.engine.gearz.game.GearzGame;
import net.cogzmc.engine.gearz.player.GearzPlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class GearzAbstractClass<PlayerType extends GearzPlayer> implements Listener {
    @Getter private final PlayerType player;
    @Getter private final GearzGame game;
    @Getter private final GearzClassMeta meta;

    public GearzAbstractClass(PlayerType player, GearzGame game) {
        this.player = player;
        this.game = game;
        this.meta = getClass().getAnnotation(GearzClassMeta.class);
        onConstructor();
    }

    protected void onConstructor() {}
    public void onGameStart() {}
    public void onGameEndForPlayer() {}
    public void onPlayerActivate() {}
    public void onClassDeactivate() {}
    public void onClassActivate() {}

    public void registerClass() {
        game.getPlugin().registerEvents(this);
    }

    public void deregisterClass() {
        HandlerList.unregisterAll(this);
    }
}
