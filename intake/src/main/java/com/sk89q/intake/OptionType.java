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

import com.google.common.collect.ImmutableList;
import com.sk89q.intake.argument.Arguments;
import com.sk89q.intake.argument.CommandArgs;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines the type of parameter, whether it is positional, a flag, optional,
 * or required.
 */
public abstract class OptionType {

    private static final RequiredPositional REQUIRED_PARAMETER = new RequiredPositional();
    private static final OptionalPositional OPTIONAL_PARAMETER = new OptionalPositional();

    private OptionType() {
    }

    /**
     * Get the flag associated with this parameter.
     *
     * @return the flag, or null if there is no flag associated
     * @see #isValueFlag()
     */
    @Nullable
    public abstract Character getFlag();

    /**
     * Return whether the flag is a value flag.
     *
     * @return true if the flag is a value flag
     * @see #getFlag()
     */
    public abstract boolean isValueFlag();

    /**
     * Get whether this parameter is optional.
     *
     * @return true if the parameter does not have to be specified
     */
    public abstract boolean isOptional();

    /**
     * Create a new list of command arguments for the given arguments that
     * is appropriate for this type of parameter.
     *
     * <p>For example, if the type of parameter is a value flag,
     * then the returned arguments object will only have the value flag's
     * value as the argument.</p>
     *
     * @param arguments The list of arguments
     * @return The new list of arguments
     */
    public abstract CommandArgs transform(CommandArgs arguments);

    /**
     * Get the required positional type of parameter.
     *
     * @return An option type
     */
    public static OptionType positional() {
        return REQUIRED_PARAMETER;
    }

    /**
     * Get the optional positional type of parameter.
     *
     * @return An option type
     */
    public static OptionType optionalPositional() {
        return OPTIONAL_PARAMETER;
    }

    /**
     * Get the non-value boolean flag type of parameter.
     *
     * @param flag The flag character
     * @return An option type
     */
    public static OptionType flag(Character flag) {
        checkNotNull(flag, "flag");
        return new BooleanFlag(flag);
    }

    /**
     * Get the value flag type of parameter.
     *
     * @param flag The flag character
     * @return An option type
     */
    public static OptionType valueFlag(Character flag) {
        checkNotNull(flag, "flag");
        return new ValueFlag(flag);
    }

    private static final class RequiredPositional extends OptionType {
        @Nullable
        @Override
        public Character getFlag() {
            return null;
        }

        @Override
        public boolean isValueFlag() {
            return false;
        }

        @Override
        public boolean isOptional() {
            return false;
        }

        @Override
        public CommandArgs transform(CommandArgs arguments) {
            return arguments;
        }
    }

    private static final class OptionalPositional extends OptionType {
        @Nullable
        @Override
        public Character getFlag() {
            return null;
        }

        @Override
        public boolean isValueFlag() {
            return false;
        }

        @Override
        public boolean isOptional() {
            return true;
        }

        @Override
        public CommandArgs transform(CommandArgs arguments) {
            return arguments;
        }
    }

    private static final class BooleanFlag extends OptionType {
        private final Character flag;

        private BooleanFlag(Character flag) {
            this.flag = flag;
        }

        @Nullable
        @Override
        public Character getFlag() {
            return flag;
        }

        @Override
        public boolean isValueFlag() {
            return false;
        }

        @Override
        public boolean isOptional() {
            return true;
        }

        @Override
        public CommandArgs transform(CommandArgs arguments) {
            String v = arguments.getFlags().containsKey(flag) ? "true" : "false";
            return Arguments.copyOf(ImmutableList.of(v), arguments.getFlags(), arguments.getNamespace());
        }
    }

    private static final class ValueFlag extends OptionType {
        private final Character flag;

        private ValueFlag(Character flag) {
            this.flag = flag;
        }

        @Nullable
        @Override
        public Character getFlag() {
            return flag;
        }

        @Override
        public boolean isValueFlag() {
            return true;
        }

        @Override
        public boolean isOptional() {
            return true;
        }

        @Override
        public CommandArgs transform(CommandArgs arguments) {
            String value = arguments.getFlags().get(flag);
            if (value == null) {
                return Arguments.copyOf(ImmutableList.<String>of(), arguments.getFlags(), arguments.getNamespace());
            }
            return Arguments.copyOf(ImmutableList.of(value), arguments.getFlags(), arguments.getNamespace());
        }
    }

}
