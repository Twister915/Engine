package net.cogz.gearz.hub.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jake on 2/21/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HubModuleMeta {

    public String key() default "null";

    public boolean enabled() default true;

}

