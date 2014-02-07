package net.tbnr.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Created by George on 07/02/14.
 * <p/>
 * Purpose Of File: To Represent a utility method ( Not in a GUtility files, for when you make a utility method so later we can search for them to put them in a util file)
 * <p/>
 * Latest Change: Added it
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE})
public @interface GUtilityMethod {
}
