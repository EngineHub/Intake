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

import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.completion.CommandCompleter;
import com.sk89q.intake.util.auth.AuthorizationException;

import java.util.List;

/**
 * A command that can be executed.
 */
public interface CommandCallable extends CommandCompleter {

    /**
     * Execute the command.
     *
     * <p>{@code parentCommands} is a list of "parent" commands, including
     * the current command, where each deeper command is appended to
     * the list of parent commands.</p>
     *
     * <p>For example, if the command entered was {@code /world create ocean} and
     * the command in question was the "create" command, then:</p>
     *
     * <ul>
     *     <li>{@code arguments} would be {@code ocean}</li>
     *     <li>{@code parentCommands} would be {@code world create}</li>
     * </ul>
     *
     * <p>On the other hand, if the command was "world," then:</p>
     *
     * <ul>
     *     <li>{@code arguments} would be {@code create ocean}</li>
     *     <li>{@code parentCommands} would be {@code world}</li>
     * </ul>
     *
     * @param arguments The arguments
     * @param namespace Additional values used for execution
     * @param parentCommands The list of parent commands
     * @return Whether the command succeeded
     * @throws CommandException If there is an error with the command
     * @throws InvocationCommandException If there is an error with executing the command
     * @throws AuthorizationException If there is a authorization error
     */
    boolean call(String arguments, Namespace namespace, List<String> parentCommands) throws CommandException, InvocationCommandException, AuthorizationException;

    /**
     * Get the object describing the command.
     *
     * @return The object describing the command
     */
    Description getDescription();

    /**
     * Test whether the user is permitted to use the command.
     *
     * @param namespace The namespace
     * @return Whether permission is provided
     */
    boolean testPermission(Namespace namespace);

}
