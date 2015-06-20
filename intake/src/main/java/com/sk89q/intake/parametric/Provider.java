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

package com.sk89q.intake.parametric;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * An object that provides instances given a key and some arguments.
 *
 * <p>Providers do the heavy work of reading passed in arguments and
 * transforming them into Java objects.</p>
 */
public interface Provider<T> {

    /**
     * Gets whether this provider does not actually consume values
     * from the argument stack and instead generates them otherwise.
     *
     * @return Whether values are provided without use of the arguments
     */
    boolean isProvided();

    /**
     * Provide a value given the arguments.
     *
     * @param arguments The arguments
     * @param modifiers The modifiers on the parameter
     * @return The value provided
     * @throws ArgumentException If there is a problem with the argument
     * @throws ProvisionException If there is a problem with the provider
     */
    @Nullable
    T get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException;

    /**
     * Get a list of suggestions for the given parameter and user arguments.
     *
     * <p>If no suggestions could be enumerated, an empty list should
     * be returned.</p>
     *
     * @param prefix What the user has typed so far (may be an empty string)
     * @return A list of suggestions
     */
    List<String> getSuggestions(String prefix);

}
