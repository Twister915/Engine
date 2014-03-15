package net.cogz.gearz.hub.modules;

import net.cogz.gearz.hub.GearzHub;
import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerWorldParticles;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerJoinEvent;
import net.tbnr.util.player.TPlayerManager;
import net.tbnr.util.player.TPlayerStorable;
import net.tbnr.util.player.cooldowns.TCooldown;
import net.tbnr.util.player.cooldowns.TCooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/15/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
@HubModuleMeta(
        key = "snowballexp"
)
public class SnowballEXP extends HubModule implements Listener {
    public SnowballEXP() {
        super(false, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onJoin(TPlayerJoinEvent event) {
        if (event.getPlayer().isFirstJoin()) {
            event.getPlayer().giveItem(Material.SNOW_BALL, 32);
            event.getPlayer().sendMessage(GearzHub.getInstance().getFormat("formats.first-join-snowball"));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onSnowballHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Snowball ball = (Snowball) event.getDamager();
        final TPlayer thrower = TPlayerManager.getInstance().getPlayer((Player) ball.getShooter());
        final TPlayer hit = TPlayerManager.getInstance().getPlayer((Player) event.getEntity());
        if (!thrower.getPlayer().canSee(hit.getPlayer())) return;
        if (thrower.getPlayer().equals(hit.getPlayer().getPlayer())) return;
        if (hit.getPlayer().isSneaking()) {
            thrower.sendMessage(GearzHub.getInstance().getFormat("formats.snowball-evade", true, new String[]{"<player>", hit.getPlayer().getName()}));
            event.setDamage(0);
            return;
        }
        try {
            thrower.playParticleEffect(new TPlayer.TParticleEffect(hit.getPlayer().getLocation(), Gearz.getRandom().nextFloat(), 2, 15, 2, WrapperPlayServerWorldParticles.ParticleEffect.HEART, WrapperPlayServerWorldParticles.ParticleEffect.DRIP_LAVA, WrapperPlayServerWorldParticles.ParticleEffect.SMOKE));
        } catch (Exception ignored) {
        }
        thrower.playSound(Sound.CHICKEN_EGG_POP);
        thrower.playSound(Sound.ORB_PICKUP);
        thrower.getPlayer().hidePlayer(hit.getPlayer());
        GearzPlayer.playerFromTPlayer(thrower).addXp(2); //Add XP
        Bukkit.getScheduler().runTaskLater(GearzHub.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                if (thrower.getPlayer() != null && hit.getPlayer() != null) {
                    if (thrower.getPlayer().isOnline() || hit.getPlayer().isOnline())
                        thrower.getPlayer().showPlayer(hit.getPlayer());
                }
            }
        }, 200);
        event.setDamage(0);
        event.setCancelled(true);
        ball.remove();
        try {
            thrower.playParticleEffect(new TPlayer.TParticleEffect(ball.getLocation(), Gearz.getRandom().nextFloat(), 2, 5, 2, WrapperPlayServerWorldParticles.ParticleEffect.MOB_SPELL_AMBIENT));
        } catch (Exception ignored) {}
        SnowballHitStorable snowballHitStorable = new SnowballHitStorable();
        Integer hits = (Integer) thrower.getStorable(GearzHub.getInstance(), snowballHitStorable);
        if (hits == null) hits = 0;
        snowballHitStorable.setHits(hits + 1);
        thrower.store(GearzHub.getInstance(), snowballHitStorable);
        thrower.sendMessage(GearzHub.getInstance().getFormat("formats.snowball-hit", true, new String[]{"<player>", hit.getPlayer().getName()}, new String[]{"<hits>", String.valueOf(snowballHitStorable.getHits())}));
        hit.giveItem(Material.SNOW_BALL);
        SnowballHitByStorable hitByStorable = new SnowballHitByStorable();
        Integer hitBys = (Integer) hit.getStorable(GearzHub.getInstance(), hitByStorable);
        if (hitBys == null) hitBys = 0;
        hitBys += 1;
        hitByStorable.setHits(hitBys);
        hit.store(GearzHub.getInstance(), hitByStorable);
        hit.sendMessage(GearzHub.getInstance().getFormat("formats.snowball-hitby", true, new String[]{"<player>", thrower.getPlayer().getName()}, new String[]{"<hitbys>", String.valueOf(hitByStorable.getHits())}));
        hit.playSound(Sound.CHICKEN_EGG_POP);
        //event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player)) return;
        event.setCancelled(true);
        if (((Player) event.getEntity()).isSneaking()) {
            ((Player) event.getDamager()).sendMessage(GearzHub.getInstance().getFormat("formats.no-snowball-shake"));
            return;
        }
        if (!TCooldownManager.canContinue(((Player) event.getEntity()).getName() + "snowball_shakedown", new TCooldown(TimeUnit.SECONDS.toMillis(15)))) {
            ((Player) event.getDamager()).sendMessage(GearzHub.getInstance().getFormat("formats.no-snowball-shake"));
            return;
        }
        TPlayer hit = GearzHub.getInstance().getPlayerManager().getPlayer((Player) event.getEntity());
        TPlayer attacker = GearzHub.getInstance().getPlayerManager().getPlayer((Player) event.getDamager());
        if (!hit.getPlayer().getInventory().contains(Material.SNOW_BALL, 1)) {
            attacker.sendMessage(GearzHub.getInstance().getFormat("formats.no-snowball-have"));
            return;
        }
        hit.removeItem(Material.SNOW_BALL);
        attacker.giveItem(Material.SNOW_BALL);
        GearzPlayer.playerFromTPlayer(attacker).addXp(5); //Add XP
        hit.playSound(Sound.ARROW_HIT);
        attacker.playSound(Sound.CHICKEN_EGG_POP);
        attacker.sendMessage(GearzHub.getInstance().getFormat("formats.snowball-shake", true, new String[]{"<player>", hit.getPlayer().getName()}));
        hit.sendMessage(GearzHub.getInstance().getFormat("formats.snowball-shaken", true, new String[]{"<player>", attacker.getPlayer().getName()}));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onPvP(EntityDamageByEntityEvent event) {
        event.setDamage(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    private static class SnowballHitStorable implements TPlayerStorable {
        private int hits;

        private int getHits() {
            return hits;
        }

        private void setHits(int hits) {
            this.hits = hits;
        }

        @Override
        public String getName() {
            return "snowball-hits";
        }

        @Override
        public Object getValue() {
            return hits;
        }
    }

    private static class SnowballHitByStorable implements TPlayerStorable {
        private int hits;

        private int getHits() {
            return hits;
        }

        private void setHits(int hits) {
            this.hits = hits;
        }

        @Override
        public String getName() {
            return "snowball-hitbys";
        }

        @Override
        public Object getValue() {
            return hits;
        }
    }
}
