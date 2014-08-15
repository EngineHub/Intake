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

package com.sk89q.intake;

import com.sk89q.intake.completion.CommandCompleter;
import com.sk89q.intake.context.CommandLocals;
import com.sk89q.intake.util.auth.AuthorizationException;

/**
 * A command that can be executed.
 */
public interface CommandCallable extends CommandCompleter {

    /**
     * Execute the correct command based on the input.
     *
     * <p>The implementing class must perform the necessary permission
     * checks.</p>
     *
     * @param arguments the arguments
     * @param locals the locals
     * @param parentCommands a list of parent commands, with the first most entry being the top-level command
     * @return the called command, or null if there was no command found
     * @throws CommandException thrown on a command error
     */
    boolean call(String arguments, CommandLocals locals, String[] parentCommands) throws CommandException, AuthorizationException;
    
    /**
     * Get an object describing this command.
     * 
     * @return the command description
     */
    Description getDescription();

    /**
     * Test whether this command can be executed with the given context.
     *
     * @param locals the locals
     * @return true if execution is permitted
     */
    boolean testPermission(CommandLocals locals);

}
