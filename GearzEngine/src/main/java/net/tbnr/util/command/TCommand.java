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

package net.tbnr.util.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * This is the command annotation. Put it above methods that execute for commands.
 */
public @interface TCommand {
    /**
     * The name of the command
     *
     * @return Name of the command
     */
    String name();

    /**
     * The description of the command
     *
     * @return Description of the command
     */
    String description();

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
     * Aliases for alternative use of command.
     *
     * @return Aliases for alternative use of this command.
     */
    String[] aliases() default {};

    /**
     * Valid senders, only these types of senders can send this command.
     *
     * @return The valid senders.
     */
    TCommandSender[] senders();

}
