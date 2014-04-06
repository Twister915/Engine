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

import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;

/* Abstract methods of GearzGame */
public abstract class GameDelegate<PlayerType> {
    protected abstract void gameStarting();
    protected abstract void gameEnding();

    /**
     * Whether the player can build
     *
     * @param player ~ the player (in PlayerType wrapper)
     * @return boolean ~ true or false whether the player can build
     */
    protected abstract boolean canBuild(PlayerType player);

    /**
     * Whether the player can hurt other players (PvP)
     *
     * @param attacker ~ the person that attacked them
     * @param target   ~ the person that is being attacked
     * @return boolean ~ true or false whether the player can pvp
     */
    protected abstract boolean canPvP(PlayerType attacker, PlayerType target);

    protected abstract boolean canUse(PlayerType player);

    protected abstract boolean canBreak(PlayerType player, Block block);

    protected abstract boolean canPlace(PlayerType player, Block block);

    protected abstract boolean canMove(PlayerType player);

    protected abstract boolean canDrawBow(PlayerType player);

    protected abstract void playerKilled(PlayerType dead, LivingEntity killer);

    protected abstract void playerKilled(PlayerType dead, PlayerType killer);

    protected abstract void mobKilled(LivingEntity killed, PlayerType killer);

    protected abstract boolean canDropItem(PlayerType player, ItemStack itemToDrop);

    protected abstract Location playerRespawn(PlayerType player);

    protected abstract boolean canPlayerRespawn(PlayerType player);

    protected abstract void activatePlayer(PlayerType player);

    protected abstract boolean allowHunger(PlayerType player);

    protected void firstActivatePlayer(PlayerType player) {
    }

    protected double damageForHit(PlayerType attacker, PlayerType target, double initialDamage) {
        return -1;
    }

    protected boolean canPickup(PlayerType pickupee, Item item) {
        return true;
    }

    protected boolean allowEntitySpawn(Entity entity) {
        return false;
    }

    protected void removePlayerFromGame(PlayerType player) {
    }

    protected void onEggThrow(PlayerType player, PlayerEggThrowEvent event) {
    }

    protected void onSnowballThrow(PlayerType player) {
    }

    protected void onDamage(Entity damager, Entity target, EntityDamageByEntityEvent event) {}

    protected void onEntityInteract(Entity entity, EntityInteractEvent event) {}

    protected boolean useEnderBar(PlayerType player) {
        return true;
    }

    protected boolean allowInventoryChange() {
        return false;
    }

    protected void gamePreStart() {
    }

    protected void onDeath(PlayerType player) {
    }

    protected boolean canPickupEXP(PlayerType player) {
        return false;
    }

    protected boolean onFallDamage(PlayerType player, EntityDamageEvent event) {
        return false;
    }

    protected boolean canLeafsDecay() {
        return false;
    }

    protected GearzGame.Explosion getExplosionType() {
        return GearzGame.Explosion.REPAIR_BLOCK_DAMAGE_AND_NO_DROP;
    }

    protected boolean canUsePotion(PlayerType player) {
        return true;
    }

    protected void onPlayerGameEnd(PlayerType player, GameStopCause cause) {}

    protected void onPlayerBecomePlayer(PlayerType player) {}

    protected void onPlayerBecomeSpectator(PlayerType player) {}

    protected boolean canCreatePortal() {
        return false;
    }
}
