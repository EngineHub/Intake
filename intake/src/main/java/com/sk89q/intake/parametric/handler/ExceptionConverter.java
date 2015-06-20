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
import com.sk89q.intake.InvocationCommandException;

/**
 * This class converts input {@link Throwable}s into an appropriate
 * {@link CommandException}, throwing {@link InvocationCommandException} if
 * the exception cannot be converted into a friendlier exception.
 *
 * <p>The purpose of this class is to allow commands to throw
 * domain-specific exceptions without having to worry with printing
 * helpful error messages to the user as a registered instance of this class
 * will perform that job.</p>
 */
public interface ExceptionConverter {
    
    /**
     * Attempt to convert the given throwable into a friendly
     * {@link CommandException}.
     * 
     * <p>If the exception is not recognized, then
     * {@link InvocationCommandException} should be thrown to wrap the exception.</p>
     * 
     * @param t The throwable
     * @throws CommandException If there is a problem with the command
     * @throws InvocationCommandException If there is a problem with command invocation
     */
    void convert(Throwable t) throws CommandException, InvocationCommandException;

}
