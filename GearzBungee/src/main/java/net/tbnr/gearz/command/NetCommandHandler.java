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

package net.tbnr.gearz.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The NetCommandHandler annotation. Put this above a method that is to handle a specific netcommand
 * All methods must return nothing (void), accept one argument of type HashMap, which will be the arguments.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NetCommandHandler {
    /**
     * The name of the command
     *
     * @return The name of the command.
     */
    public String name();

    /**
     * The arguments of the command.
     *
     * @return The arguments.
     */
    public String[] args();
}
