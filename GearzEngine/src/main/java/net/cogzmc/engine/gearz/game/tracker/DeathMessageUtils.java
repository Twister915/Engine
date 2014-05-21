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

import net.cogzmc.engine.util.annotations.GUtility;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.tracker.trackers.base.gravity.Attack;

/**
 * Class to resolve death messages based on different
 * attacks and events.
 * <p>
 * Latest Change:
 * <p>
 *
 * @author jake
 * @since 3/30/2014
 */
public class DeathMessageUtils implements GUtility {
    public static String getEntityString(Entity entity) {
        switch (entity.getType()) {
            case BAT:
                return "a bat";
            case BLAZE:
                return "a blaze";
            case CAVE_SPIDER:
                return "a cave spider";
            case CHICKEN:
                return "a chicken";
            case COW:
                return "a cow";
            case CREEPER:
                return "a creeper";
            case ENDER_DRAGON:
                return "an Ender Dragon";
            case ENDERMAN:
                return "an enderman";
            case FALLING_BLOCK:
                return "a falling block";
            case FIREBALL:
                return "a fireball";
            case GHAST:
                return "a ghast";
            case GIANT:
                return "a giant";
            case HORSE:
                return "a horse";
            case IRON_GOLEM:
                return "an iron golem";
            case MAGMA_CUBE:
                return "a magma cube";
            case MUSHROOM_COW:
                return "a mooshroom";
            case OCELOT:
                return "an ocelot";
            case PIG:
                return "a pig";
            case PIG_ZOMBIE:
                return "a pig zombie";
            case PLAYER:
                return ((Player) entity).getDisplayName();
            case SHEEP:
                return "a sheep";
            case SILVERFISH:
                return "a silverfish";
            case SKELETON:
                return "a skeleton";
            case SLIME:
                return "a slime";
            case SMALL_FIREBALL:
                return "a fireball";
            case SNOWMAN:
                return "a snowman";
            case SPIDER:
                return "a spider";
            case SQUID:
                return "a squid";
            case VILLAGER:
                return "a villager";
            case WITCH:
                return "a witch";
            case WOLF:
                return "a wolf";
            case ZOMBIE:
                return "a zombie";
            default:
                return "an unknown entity";
        }
    }

    public static String getProjectileName(Entity entity) {
        switch (entity.getType()) {
            case PRIMED_TNT:
                return "TNT";
            case ARROW:
                return "arrow";
            case EGG:
                return "egg";
            case ENDER_PEARL:
                return "ender pearl";
            case FIREBALL:
                return "fireball";
            case SNOWBALL:
                return "snowball";
            case WITHER_SKULL:
                return "wither skull";
            default:
                return "";
        }
    }

    public static String getName(ItemStack item) {
        String niceName = item.getType().name().replaceAll("_", " ").toLowerCase();
        return (item.getEnchantments().size() > 0 ? "enchanted " : "") + niceName;
    }

    public static String getWhereFrom(Attack.From from) {
        switch (from) {
            case LADDER:
                return " off a ladder";
            case WATER:
                return " out of the water";
            default:
                return "";
        }
    }

    public static String getCauseOfAttack(Attack.Cause cause) {
        switch (cause) {
            case HIT:
                return "was knocked";
            case SHOOT:
                return "was shot";
            case SPLEEF:
                return "was spleefed";
            default:
                return "";
        }
    }

    public static String getWhereTo(Attack.From from, EntityDamageEvent.DamageCause damageCause) {
        if (from == Attack.From.FLOOR) {
            switch (damageCause) {
                case VOID:
                    return " out of the world";
                case FALL:
                    return " off a high place";
                case LAVA:
                case FIRE_TICK:
                    return " into lava";
                case SUICIDE:
                    return " to their death (suicide/combatlog)";
                default:
                    return " to their death";
            }
        } else {
            switch (damageCause) {
                case VOID:
                    return " and into the void";
                case FALL:
                    return "";
                case LAVA:
                case FIRE_TICK:
                    return " and into lava";
                case SUICIDE:
                    return " to their death";
                default:
                    return " to their death";
            }
        }
    }

    public static String getDamageAction(EntityDamageEvent.DamageCause damageCause) {
        switch (damageCause) {
            case ENTITY_ATTACK:
                return "was slain";
            case PROJECTILE:
                return "was shot";
            case BLOCK_EXPLOSION:
                return "was blown up";
            case CONTACT:
                return "was pricked to death";
            case DROWNING:
                return "drowned";
            case FALL:
                return "hit the ground too hard";
            case FIRE:
                return "burned to death";
            case FIRE_TICK:
            case LAVA:
                return "died in lava";
            case LIGHTNING:
                return "was struck by lightning";
            case POISON:
                return "was poisoned";
            case STARVATION:
                return "starved to death";
            case SUFFOCATION:
                return "suffocated in a wall";
            case VOID:
                return "fell out of the world";
            default:
                return "died";
        }
    }
}
