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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/**
 * Builds instances of {@link CommandArgs}.
 */
public final class Arguments {

    private Arguments() {
    }

    /**
     * Create an argument stack from a CommandContext.
     *
     * @param context The instance of a CommandContext
     * @return The arguments
     */
    public static CommandArgs viewOf(CommandContext context) {
        return new ContextArgs(context);
    }

    /**
     * Create an argument stack from a list of string arguments using
     * an empty namespace.
     *
     * @param arguments The list of string arguments
     * @return The arguments
     */
    public static CommandArgs copyOf(List<String> arguments) {
        return new StringListArgs(arguments, ImmutableMap.<Character, String>of(), new Namespace());
    }

    /**
     * Create an argument stack from a list of string arguments using
     * an empty namespace.
     *
     * @param arguments The array of string arguments
     * @return The arguments
     */
    public static CommandArgs of(String... arguments) {
        return copyOf(ImmutableList.copyOf(arguments));
    }

    /**
     * Create an argument stack from a list of string arguments.
     *
     * @param arguments The list of string arguments
     * @param flags A map of flags, where the key is the flag and the value may be null
     * @return The arguments
     */
    public static CommandArgs copyOf(List<String> arguments, Map<Character, String> flags) {
        return new StringListArgs(arguments, flags, new Namespace());
    }

    /**
     * Create an argument stack from a list of string arguments.
     *
     * @param arguments The list of string arguments
     * @param flags A map of flags, where the key is the flag and the value may be null
     * @param namespace The associated namespace
     * @return The arguments
     */
    public static CommandArgs copyOf(List<String> arguments, Map<Character, String> flags, Namespace namespace) {
        return new StringListArgs(arguments, flags, namespace);
    }

}
