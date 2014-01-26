package net.gearz.settings.type;

import com.google.common.base.Preconditions;

/**
 * Utilities for parsing settings
 */
public class SettingTypeUtil {
    /**
     * Gets the value of an object as a class
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(Object value, Class<T> typeClass) throws IllegalArgumentException {
        Preconditions.checkNotNull(typeClass);
        if (value != null) {
            Preconditions.checkArgument(value.getClass().isAssignableFrom(typeClass), "value may not be cast to %s", typeClass.getName());
            return (T) value;
        } else {
            return null;
        }
    }

    /**
     * Checks if a object is an instance of a setting type
     * @param type setting type to check
     * @param obj object to check
     * @throws IllegalArgumentException thrown on IllegalCastException between the obj and the type
     */
    public static void checkInstance(SettingType type, Object obj) throws IllegalArgumentException {
        Preconditions.checkNotNull(obj, "object may not be null");
        Preconditions.checkArgument(type.isInstance(obj), "object is not an instance of " + type.getName());
    }
}
