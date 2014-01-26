package net.gearz.settings.type;

/**
 * Implementable when a setting can be toggled through
 */
public interface Toggleable {
    Object getNextState(Object previous) throws IllegalArgumentException;
}
