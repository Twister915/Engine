package net.gearz.settings.type;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to store the name of a setting
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    String value();
}
