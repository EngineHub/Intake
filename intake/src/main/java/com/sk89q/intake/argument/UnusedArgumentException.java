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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Thrown when there are unused arguments because the user has provided
 * excess arguments.
 */
public class UnusedArgumentException extends ArgumentException {
    
    private String unconsumed;

    /**
     * Create a new instance with the unconsumed argument data.
     *
     * @param unconsumed The unconsumed arguments
     */
    public UnusedArgumentException(String unconsumed) {
        super("Unconsumed arguments: " + unconsumed);
        checkNotNull(unconsumed);
        this.unconsumed = unconsumed;
    }

    /**
     * Get the unconsumed arguments.
     *
     * @return The unconsumed arguments
     */
    public String getUnconsumed() {
        return unconsumed;
    }

}
