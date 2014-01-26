package net.tbnr.gearz.settings;

import net.gearz.settings.SettingBuilder;
import net.gearz.settings.base.BaseSetting;
import net.gearz.settings.base.BaseSettingsRegistry;
import net.gearz.settings.type.BooleanType;

/**
 * Stores Gearz base settings
 */
public final class SettingsRegistration {

    /**
     * Register the settings stored in this class
     */
    public static void register() {
        BaseSettingsRegistry settingsRegistry = PlayerSettings.getRegistry();
        settingsRegistry.register(SettingsRegistration.JOIN_MESSAGES);
    }

    /**
     * Join Messages Settings
     */
    public static BaseSetting JOIN_MESSAGES = new SettingBuilder()
            .name("JoinMessages")
            .type(new BooleanType())
            .defaultValue(false)
            .description("Toggle the display of join messages.").get();
}
