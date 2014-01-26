package net.gearz.settings.type;

/**
 * SettingType that can be toggeled
 * as either true of false
 */
public class BooleanType implements SettingType, Toggleable {
    @Override
    public String getName() {
        return "Boolean";
    }

    @Override
    public boolean isInstance(Object obj) {
        return obj instanceof Boolean;
    }

    @Override
    public Object parse(String raw) {
        raw = raw.toLowerCase().trim();
        switch (raw) {
            case "on":
            case "true":
            case "yes":
                return true;
            case "off":
            case "false":
            case "no":
                return false;
            default:
                throw new IllegalArgumentException("unknown option '" + raw + "'");
        }
    }

    @Override
    public Object getNextState(Object previous) throws IllegalArgumentException {
        Boolean value = SettingTypeUtil.getValue(previous, Boolean.class);
        return !value;
    }
}
