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

package net.tbnr.gearz.game.tracker;

import net.tbnr.gearz.game.GearzGame;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.tracker.TrackerManager;
import tc.oc.tracker.Trackers;
import tc.oc.tracker.trackers.ExplosiveTracker;
import tc.oc.tracker.trackers.FireEnchantTracker;
import tc.oc.tracker.trackers.ProjectileDistanceTracker;
import tc.oc.tracker.trackers.base.gravity.Attack;
import tc.oc.tracker.trackers.base.gravity.SimpleGravityKillTracker;

import static java.lang.Math.round;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author jake
 * @since 3/30/2014
 */
public class DeathMessageProcessor {
    private PlayerDeathEvent event;
    private GearzGame game;

    public DeathMessageProcessor(PlayerDeathEvent event, GearzGame game) {
        this.game = game;
        this.event = event;
    }

    public PlayerDeath processDeath() {
        PlayerDeath playerDeath = new PlayerDeath(event.getEntity(), game);

        TrackerManager tracker = Trackers.getManager();
        SimpleGravityKillTracker gravity = tracker.getTracker(SimpleGravityKillTracker.class);
        ExplosiveTracker explosive = tracker.getTracker(ExplosiveTracker.class);
        ProjectileDistanceTracker projectile = tracker.getTracker(ProjectileDistanceTracker.class);
        FireEnchantTracker fire = tracker.getTracker(FireEnchantTracker.class);

        processDefaults(playerDeath);
        processFire(fire, playerDeath);
        processGravity(gravity, playerDeath);
        processExplosives(explosive, playerDeath);
        processWeapon(playerDeath);
        processProjectiles(projectile, playerDeath);
        return playerDeath;
    }

    protected void processDefaults(PlayerDeath death) {
        death.setEvent(death.getVictim().getLastDamageCause());
        if (death.getEvent() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) death.getEvent();
            Entity killerEntity = entityEvent.getDamager();

            if (killerEntity instanceof LivingEntity) {
                LivingEntity killer = (LivingEntity) killerEntity;
                death.setKiller(killer);
            }
        }

        String attack = DeathMessageUtils.getDamageAction(death.getCause());
        death.setAction(attack);
    }

    protected void processFire(FireEnchantTracker fire, PlayerDeath death) {
        Player p = death.getVictim();
        if (death.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if (fire.hasOriginalDamager(p)) {
                death.setAction("was fried");
                death.setKiller(fire.getOriginalDamager(p));
            }
        }

    }

    protected void processGravity(SimpleGravityKillTracker gravity, PlayerDeath death) {
        Player p = death.getVictim();

        if (gravity.attacks.containsKey(p)) {
            Attack attack = gravity.attacks.remove(p);
            if (gravity.wasAttackFatal(attack, death.getCause(), 200)) {
                EntityDamageEvent.DamageCause damageCause = death.getCause();

                death.setAction(DeathMessageUtils.getCauseOfAttack(attack.cause));
                death.setFrom(DeathMessageUtils.getWhereFrom(attack.from));
                death.setTo(DeathMessageUtils.getWhereTo(attack.from, damageCause));
                death.setKiller(attack.attacker);
            }
        }
    }

    protected void processExplosives(ExplosiveTracker explosive, PlayerDeath death) {
        if (death.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            if (((EntityDamageByEntityEvent) death.getEvent()).getDamager() instanceof TNTPrimed) {
                TNTPrimed tnt = (TNTPrimed) ((EntityDamageByEntityEvent) death.getEvent()).getDamager();
                if (explosive.getOwner(tnt) != null) {
                    Player killer = explosive.getOwner(tnt);
                    death.setAction("was exploded");
                    death.setItemStack("TNT");
                    death.setKiller(killer);
                }
            }
        }
    }

    protected void processWeapon(PlayerDeath death) {
        if (death.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (death.getCredited() != null) {
                String weapon = "";
                ItemStack item = death.getCredited().getItemInHand();
                if (item.getType() == Material.AIR) {
                    weapon = "fists";
                } else {
                    if (DeathMessageUtils.getName(item) != null) {
                        weapon = DeathMessageUtils.getName(item);
                    }
                }
                death.setItemStack(weapon);
            }
        }
    }

    protected void processProjectiles(ProjectileDistanceTracker tracker, PlayerDeath death) {
        if (death.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) death.getEvent();
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Entity) {
                Entity shooter = (Entity) projectile.getShooter();
                double distance = projectile.getLocation().distance(tracker.getLaunchLocation(projectile));

                death.setKiller(shooter);
                death.setMisc("(" + round(distance) + " blocks)");
            }
        }
    }

}
