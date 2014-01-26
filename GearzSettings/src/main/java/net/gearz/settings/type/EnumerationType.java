package net.gearz.settings.type;

import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Setting type that can be toggled through enum values
 */
public class EnumerationType<T extends Enum> implements SettingType, Toggleable {
    private final String name;
    private final Class<T> enumClass;
    private final HashMap<T, String> nameMapping;

    public EnumerationType(String name, Class<T> enumClass) {
        this.name = name;
        this.enumClass = enumClass;

        HashMap<T, String> tempMapping = Maps.newHashMap();
        for (Field field : enumClass.getFields()) {
            if (field.isEnumConstant()) {
                @SuppressWarnings("unchecked")
                T value = (T) Enum.valueOf(this.enumClass, field.getName());

                Name declaredEnumName = field.getAnnotation(Name.class);
                if (declaredEnumName != null) {
                    tempMapping.put(value, declaredEnumName.value());
                } else {
                    tempMapping.put(value, field.getName());
                }
            }
        }
        this.nameMapping = tempMapping;
    }

    public String getName() {
        return "Enum of " + this.name;
    }

    public boolean isInstance(Object obj) {
        return this.enumClass.isInstance(obj);
    }

    public Object parse(String raw) throws IllegalArgumentException {
        T obj = this.findByName(raw);

        if (obj != null) {
            return obj;
        } else {
            throw new IllegalArgumentException("unknown option '" + raw + "'");
        }
    }

    private T findByName(String search) {
        for (Map.Entry<T, String> entry : this.nameMapping.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(search)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Object getNextState(Object previous) throws IllegalArgumentException {
        SettingTypeUtil.checkInstance(this, previous);

        T[] constants = this.enumClass.getEnumConstants();

        int index = Arrays.asList(this.enumClass.getEnumConstants()).indexOf(previous);
        if (index < 0) {
            throw new IllegalArgumentException("previous is not an enum constant");
        }

        int newIndex = index + 1;
        if (newIndex >= constants.length) {
            newIndex = 0;
        }

        return constants[newIndex];
    }
}
