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

package net.tbnr.util.bungee.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TCommand {
    /**
     * The name of the command
     *
     * @return Name of the command
     */
    String name();

    /**
     * The usage of the command.
     *
     * @return This is the help text for the command, should be handled by the plugin.
     */
    String usage();

    /**
     * Permission to use command.
     *
     * @return Permission to use this command.
     */
    String permission();

    /**
     * Valid senders, only these types of senders can send this command.
     *
     * @return The valid senders.
     */
    TCommandSender[] senders();

    /**
     * Aliases
     *
     * @return Aliases
     */
    String[] aliases() default {};
}
