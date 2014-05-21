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

package net.cogzmc.engine.gearz.effects;

import net.cogzmc.engine.gearz.Gearz;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a static firework that you can shoot off in a variant of modes. Reproducible firework effects with ease!
 */
@SuppressWarnings("unused")
public final class GearzFireworkEffect {
    /**
     * The location at which the firework is fired
     */
    private final Location location;
    /**
     * The effects being used by this FireworkEffect
     */
    private final List<FireworkEffect> fireworkEffects;
    /**
     * Stores the index for all modes to allow sequencing.
     */
    private int index = 0;

    /**
     * Different selection modes for which effects ar used by the fire() methods.
     */
    public static enum SelectionMode {
        /**
         * Will choose random effects until your quota passed is reached.
         */
        Random,
        /**
         * Will dump effects starting at 0 until your count is met. Use the single argument method to fire all effects at once.
         */
        All,
        /**
         * Fires the next firework (using internal counter to determine what is next in the sequence).
         */
        Sequence
    }

    /**
     * Constructor of a FireworkEffect
     *
     * @param location        The location at which this effect will be fired
     * @param fireworkEffects The effects to use when firing the firework.
     */
    public GearzFireworkEffect(Location location, List<FireworkEffect> fireworkEffects) {
        this.fireworkEffects = fireworkEffects;
        this.location = location;
    }

    /**
     * Constructor of a FireworkEffect with variable arguments
     *
     * @param location         The location at which to fire the firework
     * @param fireworkEffects1 The effects to use
     */
    @SuppressWarnings("unused")
    public GearzFireworkEffect(Location location, FireworkEffect... fireworkEffects1) {
        this(location, Arrays.asList(fireworkEffects1));
    }

    /**
     * Adds effect(s) to the loop
     *
     * @param effects The effects to add
     */
    @SuppressWarnings("unused")
    public void addEffect(FireworkEffect... effects) {
        this.fireworkEffects.addAll(Arrays.asList(effects));
    }

    /**
     * Fires the firework once
     *
     * @param mode   The mode in which to fire.
     * @param count1 The number of effects to combine.
     */
    public void fire(SelectionMode mode, int count1) {
        int count = (count1 > this.fireworkEffects.size()) ? this.fireworkEffects.size() : count1;
        List<FireworkEffect> activeEffects = new ArrayList<>();
        switch (mode) {
            case Random:
                while (activeEffects.size() < count) {
                    activeEffects.add(this.fireworkEffects.get(Gearz.getRandom().nextInt(this.fireworkEffects.size())));
                    increaseIndex();
                }
                break;
            case All:
                for (int i = 0; i < count; i++) {
                    activeEffects.add(this.fireworkEffects.get(i));
                    increaseIndex();
                }
                break;
            case Sequence:
                while (activeEffects.size() < count) {
                    activeEffects.add(this.fireworkEffects.get(index));
                    increaseIndex();
                }
                break;
            default:
                activeEffects.addAll(this.fireworkEffects);
                index = 0;
        }
        Firework f = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = f.getFireworkMeta();
        meta.addEffects(activeEffects);
        f.setFireworkMeta(meta);
    }

    /**
     * Used to do index logic and increment index.
     */
    private void increaseIndex() {
        index++;
        if (index == fireworkEffects.size()) {
            index = 0;
        }
    }

    /**
     * Fires all effects in a mode
     *
     * @param mode The mode in which to fire.
     */
    @SuppressWarnings("unused")
    public void fire(SelectionMode mode) {
        this.fire(mode, this.fireworkEffects.size());
    }
}
