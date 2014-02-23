package net.cogz.gearz.hub.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by George on 23/01/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HubItemMeta {

    public String key() default "null";

    public boolean hidden() default false;

    public String permission() default "";

}
