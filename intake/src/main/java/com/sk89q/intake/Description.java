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

import javax.annotation.Nullable;
import java.util.List;

/**
 * A description of a command, providing information on the command's
 * parameters, a short description, a help text, and usage information.
 * However, it is up for implementations to provide the information &mdash;
 * some implementations may provide very little information.
 *
 * <p>This class does not define a way to execute the command. See
 * {@link CommandCallable}, which has a {@code getDescription()} method,
 * for an interface that does define how a command is executed.</p>
 */
public interface Description {

    /**
     * Get the list of parameters for this command.
     * 
     * @return A list of parameters
     */
    List<Parameter> getParameters();

    /**
     * Get a short one-line description of this command.
     * 
     * @return A description, or null if no description is available
     */
    @Nullable
    String getShortDescription();

    /**
     * Get a longer help text about this command.
     * 
     * @return A help text, or null if no help is available
     */
    @Nullable
    String getHelp();

    /**
     * Get the usage string of this command.
     * 
     * <p>A usage string may look like 
     * {@code [-w &lt;world&gt;] &lt;var1&gt; &lt;var2&gt;}.</p>
     * 
     * @return A usage string
     */
    String getUsage();
    
    /**
     * Get a list of permissions that the player may have to have permission.
     * 
     * <p>Permission data may or may not be available. This is only useful as a
     * potential hint.</p>
     * 
     * @return The list of permissions
     */
    List<String> getPermissions();

}