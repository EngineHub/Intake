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
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An immutable implementation of a Description.
 *
 * <p>Use {@link Builder} to create instances.</p>
 */
public final class ImmutableDescription implements Description {
    
    private final List<Parameter> parameters;
    private final List<String> permissions;
    private final String shortDescription;
    private final String help;
    @Nullable
    private final String usageOverride;

    private ImmutableDescription(List<Parameter> parameters, List<String> permissions, String shortDescription, String help, @Nullable String usageOverride) {
        this.parameters = ImmutableList.copyOf(parameters);
        this.permissions = ImmutableList.copyOf(permissions);
        this.shortDescription = shortDescription;
        this.help = help;
        this.usageOverride = usageOverride;
    }

    @Override
    public List<Parameter> getParameters() {
        return parameters;
    }

    @Nullable
    @Override
    public String getShortDescription() {
        return shortDescription;
    }

    @Nullable
    @Override
    public String getHelp() {
        return help;
    }

    @Override
    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public String getUsage() {
        if (usageOverride != null) {
            return usageOverride;
        }
        
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        
        for (Parameter parameter : parameters) {
            if (!first) {
                builder.append(" ");
            }
            builder.append(parameter.getName());
            first = false;
        }
        
        return builder.toString();
    }

    @Override
    public String toString() {
        return getUsage();
    }

    /**
     * Builds instances of {@link ImmutableDescription}.
     *
     * <p>By default, the list of parameters and permissions will
     * be empty lists.</p>
     */
    public static class Builder {
        private List<Parameter> parameters = ImmutableList.of();
        private List<String> permissions = ImmutableList.of();
        @Nullable
        private String shortDescription;
        @Nullable
        private String help;
        @Nullable
        private String usageOverride;

        /**
         * Get the list of parameters.
         *
         * @return The list of parameters
         */
        public List<Parameter> getParameters() {
            return parameters;
        }

        /**
         * Set the list of parameters.
         *
         * @param parameters The list of parameters
         * @return The builder
         */
        public Builder setParameters(List<Parameter> parameters) {
            checkNotNull(parameters, "parameters");
            this.parameters = parameters;
            return this;
        }

        /**
         * Get a list of permissions.
         *
         * @return The list of permissions
         */
        public List<String> getPermissions() {
            return permissions;
        }

        /**
         * Set the list of permissions.
         *
         * @param permissions The list of permissions
         * @return The builder
         */
        public Builder setPermissions(List<String> permissions) {
            checkNotNull(permissions, "permissions");
            this.permissions = permissions;
            return this;
        }

        /**
         * Get the short description.
         *
         * @return The builder
         */
        @Nullable
        public String getShortDescription() {
            return shortDescription;
        }

        /**
         * Set the short description.
         *
         * @param shortDescription The short description.
         * @return The builder
         */
        public Builder setShortDescription(@Nullable String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        /**
         * Get the help text.
         *
         * @return The help text
         */
        @Nullable
        public String getHelp() {
            return help;
        }

        /**
         * Set the help text.
         *
         * @param help The help text
         * @return The builder
         */
        public Builder setHelp(@Nullable String help) {
            this.help = help;
            return this;
        }

        /**
         * Get the usage override string.
         *
         * <p>If null, then usage information will be generated
         * automatically.</p>
         *
         * @return The usage override
         */
        @Nullable
        public String getUsageOverride() {
            return usageOverride;
        }

        /**
         * Set the usage override string.
         *
         * <p>If null, then usage information will be generated
         * automatically.</p>
         *
         * @param usageOverride The usage override
         * @return The builder
         */
        public Builder setUsageOverride(@Nullable String usageOverride) {
            this.usageOverride = usageOverride;
            return this;
        }

        /**
         * Build an instance of the description.
         *
         * @return The description
         */
        public ImmutableDescription build() {
            return new ImmutableDescription(parameters, permissions, shortDescription, help, usageOverride);
        }
    }

}
