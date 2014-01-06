package net.tbnr.gearz.activerecord;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate all fields that you wish to put into the database
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BasicField {
    /**
     * Key
     *
     * @return The key to be used in the database.
     */
    public String key() default "";

}
