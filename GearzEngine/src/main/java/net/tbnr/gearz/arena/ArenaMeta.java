package net.tbnr.gearz.arena;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Joey on 1/12/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ArenaMeta {
    public String[] meta();
}
