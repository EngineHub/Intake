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

package com.sk89q.intake.argument;

import com.sk89q.intake.parametric.Provider;

import java.util.Map;

/**
 * Provides access to provided user arguments as a stack.
 */
public interface CommandArgs {

    /**
     * Tests whether there are additional arguments that can be read.
     *
     * @return Whether there are additional arguments
     */
    boolean hasNext();

    /**
     * Read the next argument.
     *
     * @return The next argument
     * @throws MissingArgumentException Thrown if there are no remaining arguments
     */
    String next() throws MissingArgumentException;

    /**
     * Read the next argument as an integer.
     *
     * @return The next argument as an integer
     * @throws MissingArgumentException Thrown if there are no remaining arguments
     * @throws ArgumentParseException Thrown if the next argument could not be parsed to an integer
     */
    int nextInt() throws MissingArgumentException, ArgumentParseException;

    /**
     * Read the next argument as a short.
     *
     * @return The next argument as an short
     * @throws MissingArgumentException Thrown if there are no remaining arguments
     * @throws ArgumentParseException Thrown if the next argument could not be parsed to an short
     */
    short nextShort() throws MissingArgumentException, ArgumentParseException;

    /**
     * Read the next argument as a byte.
     *
     * @return The next argument as an byte
     * @throws MissingArgumentException Thrown if there are no remaining arguments
     * @throws ArgumentParseException Thrown if the next argument could not be parsed to an byte
     */
    byte nextByte() throws MissingArgumentException, ArgumentParseException;

    /**
     * Read the next argument as a double.
     *
     * @return The next argument as an double
     * @throws MissingArgumentException Thrown if there are no remaining arguments
     * @throws ArgumentParseException Thrown if the next argument could not be parsed to an double
     */
    double nextDouble() throws MissingArgumentException, ArgumentParseException;

    /**
     * Read the next argument as a float.
     *
     * @return The next argument as an float
     * @throws MissingArgumentException Thrown if there are no remaining arguments
     * @throws ArgumentParseException Thrown if the next argument could not be parsed to an float
     */
    float nextFloat() throws MissingArgumentException, ArgumentParseException;

    /**
     * Read the next argument as a boolean.
     *
     * @return The next argument as an boolean
     * @throws MissingArgumentException Thrown if there are no remaining arguments
     * @throws ArgumentParseException Thrown if the next argument could not be parsed to an boolean
     */
    boolean nextBoolean() throws MissingArgumentException, ArgumentParseException;

    /**
     * Return the next argument without moving the pointer.
     *
     * @return The next argument
     * @throws MissingArgumentException Thrown if there are no remaining arguments
     */
    String peek() throws MissingArgumentException;

    /**
     * Get the current position of the pointer in the stack of arguments.
     *
     * <p>The current position indicates the value that will be returned by the
     * next call to {@link #next()} and it starts at 0. If the current position
     * is equal to {@link #size()}, then there are no more arguments
     * available.</p>
     *
     * @return The current position, starting from 0
     */
    int position();

    /**
     * Return the number of arguments in total.
     *
     * @return The number of arguments
     */
    int size();

    /**
     * Move the pointer to the end so that there are no unconsumed arguments.
     */
    void markConsumed();

    /**
     * Get a map of defined flags.
     *
     * <p>Keys are the flag (case-sensitive) and values are the values for
     * the flag. For boolean flags, the value should be the string "true". Flags
     * are commonly defined by the player by using syntax similar to
     * {@code -X value}.</p>
     *
     * @return The map of flags
     */
    Map<Character, String> getFlags();

    /**
     * Get the map of provided values.
     *
     * <p>The keys and values in a Namespace are defined before command
     * parsing has begun and they can be used by commands or
     * {@link Provider} to get session-related values.</p>
     *
     * @return The map of provided values
     */
    Namespace getNamespace();

}