/*
 * Intake, a command processing library
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) Intake team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.intake.parametric.handler;

import com.sk89q.intake.CommandException;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.ArgumentParser;
import com.sk89q.intake.parametric.ParametricBuilder;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * An invoke handler can be registered on a {@link ParametricBuilder} to
 * listen in on commands being executed.
 *
 * <p>Invoke handlers have access to three different stages of command
 * execution and can view the annotations, parameters, and arguments of the
 * command. An invoke handler could be used to implement command logging,
 * for example.</p>
 */
public interface InvokeHandler {

    /**
     * Called before arguments have been parsed.
     *
     * @param annotations The list of annotations on the command
     * @param parser The argument parser with parameter information
     * @param commandArgs The arguments provided by the user
     * @return Whether command execution should continue
     * @throws CommandException Thrown if there is a general command problem
     * @throws ArgumentException Thrown is there is an error with the arguments
     */
    boolean preProcess(List<? extends Annotation> annotations, ArgumentParser parser, CommandArgs commandArgs) throws CommandException, ArgumentException;

    /**
     * Called after arguments have been parsed but the command has yet
     * to be executed.
     *
     * @param annotations The list of annotations on the command
     * @param parser The argument parser with parameter information
     * @param args The result of the parsed arguments: Java objects to be passed to the command
     * @param commandArgs The arguments provided by the user
     * @return Whether command execution should continue
     * @throws CommandException Thrown if there is a general command problem
     * @throws ArgumentException Thrown is there is an error with the arguments
     */
    boolean preInvoke(List<? extends Annotation> annotations, ArgumentParser parser, Object[] args, CommandArgs commandArgs) throws CommandException, ArgumentException;

    /**
     * Called after the command has been executed.
     *
     * @param annotations The list of annotations on the command
     * @param parser The argument parser with parameter information
     * @param args The result of the parsed arguments: Java objects to be passed to the command
     * @param commandArgs The arguments provided by the user
     * @throws CommandException Thrown if there is a general command problem
     * @throws ArgumentException Thrown is there is an error with the arguments
     */
    void postInvoke(List<? extends Annotation> annotations, ArgumentParser parser, Object[] args, CommandArgs commandArgs) throws CommandException, ArgumentException;

}
