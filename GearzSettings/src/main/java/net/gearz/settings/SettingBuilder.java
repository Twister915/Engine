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

package net.gearz.settings;

import net.gearz.settings.base.BaseSetting;
import net.gearz.settings.type.SettingType;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for settings
 */
public class SettingBuilder {
    private String name = null;
    private List<String> aliases = new ArrayList<>();
    private String description = null;
    private SettingType type = null;
    private Object defaultValue = null;

    public SettingBuilder() {
    }

    public SettingBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SettingBuilder aliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public SettingBuilder description(String description) {
        this.description = description;
        return this;
    }

    public SettingBuilder type(SettingType type) {
        this.type = type;
        return this;
    }

    public SettingBuilder defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public BaseSetting get() {
        return new Setting(this.name, this.aliases, this.description, this.type, this.defaultValue);
    }
}
