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
import com.sk89q.intake.context.CommandContext;
import com.sk89q.intake.parametric.ParameterData;
import com.sk89q.intake.parametric.ParameterException;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.UnconsumedParameterException;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * Called before and after a command is invoked for commands executed by a
 * command created with {@link ParametricBuilder}.
 * 
 * <p>Invocation handlers are created by {@link InvokeListener}s. Multiple
 * listeners and handlers can be registered, and all be run. However, if one handler
 * throws an exception, future handlers will not execute and the command will
 * not execute (if thrown in 
 * {@link #preInvoke(Set, List, Object[], CommandContext)}.</p>
 * 
 * @see InvokeListener the factory
 */
public interface InvokeHandler {

    /**
     * Called before parameters are processed.
     *
     * @param annotations set of annotations that apply to the command
     * @param parameters the list of parameters
     * @param context the context
     * @throws CommandException can be thrown for an error, which will stop invocation
     * @throws ParameterException on parameter error
     */
    boolean preProcess(Set<Annotation> annotations, List<? extends ParameterData<?>> parameters,
                    CommandContext context) throws CommandException, ParameterException, UnconsumedParameterException;

    /**
     * Called before the parameter is invoked.
     *
     * @param annotations set of annotations that apply to the command
     * @param parameters the list of parameters
     * @param args the arguments to be given to the method
     * @param context the context
     * @return true to permit invocation of command
     * @throws CommandException can be thrown for an error, which will stop invocation
     * @throws ParameterException on parameter error
     */
    boolean preInvoke(Set<Annotation> annotations, List<? extends ParameterData<?>> parameters,
                   Object[] args, CommandContext context) throws CommandException, ParameterException, UnconsumedParameterException;

    /**
     * Called after the parameter is invoked.
     *
     * @param annotations set of annotations that apply to the command
     * @param parameters the list of parameters
     * @param args the arguments to be given to the method
     * @param context the context
     * @throws CommandException can be thrown for an error
     * @throws ParameterException on parameter error
     */
    void postInvoke(Set<Annotation> annotations, List<? extends ParameterData<?>> parameters,
                    Object[] args, CommandContext context) throws CommandException, ParameterException, UnconsumedParameterException;

}
