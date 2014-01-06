package net.tbnr.gearz.arena;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/1/13
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ArenaField {
    public String key();

    public boolean loop() default true;

    public String longName();

    public PointType type();

    public static enum PointType {
        Block,
        Player
    }
}
