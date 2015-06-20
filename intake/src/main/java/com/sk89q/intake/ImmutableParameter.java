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

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An immutable implementation of {@link Parameter}.
 */
public final class ImmutableParameter implements Parameter {
    
    private final String name;
    @Nullable
    private final OptionType optionType;
    private final List<String> defaultValue;

    private ImmutableParameter(String name, OptionType optionType, List<String> defaultValue) {
        this.name = name;
        this.optionType = optionType;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OptionType getOptionType() {
        return optionType;
    }

    @Override
    public List<String> getDefaultValue() {
        return defaultValue;
    }

    /**
     * Creates instances of {@link ImmutableParameter}.
     *
     * <p>By default, the default value will be an empty list.</p>
     */
    public static class Builder {
        private String name;
        private OptionType optionType;
        private List<String> defaultValue = Collections.emptyList();

        /**
         * Get the name of the parameter.
         *
         * @return The name of the parameter
         */
        public String getName() {
            return name;
        }

        /**
         * Set the name of the parameter.
         *
         * @param name The name of the parameter
         * @return The builder
         */
        public Builder setName(String name) {
            checkNotNull(name, "name");
            this.name = name;
            return this;
        }

        /**
         * Get the type of parameter.
         *
         * @return The type of parameter
         */
        public OptionType getOptionType() {
            return optionType;
        }

        /**
         * Set the type of parameter.
         *
         * @param optionType The type of parameter
         * @return The builder
         */
        public Builder setOptionType(OptionType optionType) {
            checkNotNull(optionType, "optionType");
            this.optionType = optionType;
            return this;
        }

        /**
         * Get the default value as a list of arguments.
         *
         * <p>An empty list implies that there is no default value.</p>
         *
         * @return The default value (one value) as a list
         */
        public List<String> getDefaultValue() {
            return defaultValue;
        }

        /**
         * Set the default value as a list of arguments.
         *
         * <p>An empty list implies that there is no default value.</p>
         *
         * @param defaultValue The default value (one value) as a list
         * @return The builder
         */
        public Builder setDefaultValue(List<String> defaultValue) {
            checkNotNull(defaultValue, "defaultValue");
            this.defaultValue = ImmutableList.copyOf(defaultValue);
            return this;
        }

        /**
         * Create an instance.
         *
         * <p>Neither {@code name} nor {@code optionType} can be null.</p>
         *
         * @return The instance
         */
        public ImmutableParameter build() {
            checkNotNull(name, "name");
            checkNotNull(optionType, "optionType");
            return new ImmutableParameter(name, optionType, defaultValue);
        }
    }
}
