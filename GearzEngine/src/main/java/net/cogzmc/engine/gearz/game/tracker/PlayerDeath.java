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

package net.cogzmc.engine.gearz.game.tracker;

import lombok.Data;
import lombok.ToString;
import net.cogzmc.engine.gearz.game.GearzGame;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author jake
 * @since 3/30/2014
 */
@ToString(exclude = {"game"})
@Data
public class PlayerDeath {
    private Player victim;
    private Entity killer;
    private String action;
    private String itemStack;
    private String from;
    private String to;
    private EntityDamageEvent event;
    private String misc;
    private GearzGame game;

    public PlayerDeath(Player victim, GearzGame game) {
        this.victim = victim;
        this.killer = null;
        this.action = "";
        this.itemStack = "";
        this.from = "";
        this.to = "";
        this.event = null;
        this.misc = "";
        this.game = game;
    }

    public String getKillerName() {
        return (killer != null) ? DeathMessageUtils.getEntityString(killer) : "";
    }

    public Player getCredited() {
        return (killer instanceof Player) ? (Player) killer : null;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return event.getCause();
    }

    public String getDeathMessage() {
        return
                game.getGameMeta().mainColor()
                        + victim.getDisplayName()
                        + " "
                        + game.getGameMeta().secondaryColor()
                        + action
                        + from
                        + to
                        + (killer == null ? "" : " by ")
                        + game.getGameMeta().mainColor()
                        + getKillerName()
                        + game.getGameMeta().secondaryColor()
                        + (itemStack == null || itemStack.equals("") ? "" : "'s " + itemStack)
                        + (misc.equals("") || misc == null ? "" : " " + misc)
                        + ".";
    }
}