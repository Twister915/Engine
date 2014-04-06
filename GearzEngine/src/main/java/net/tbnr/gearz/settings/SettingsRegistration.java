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

package net.tbnr.gearz.settings;

import com.google.common.collect.Lists;
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
            .defaultValue(true)
            .description("Toggle the display of join messages.")
            .aliases(Lists.newArrayList("jm"))
            .get();
}
