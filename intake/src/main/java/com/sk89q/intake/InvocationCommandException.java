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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Thrown when invocation of a command fails, wrapping the exception that
 * is thrown.
 *
 * <p>{@link #getCause()} will not be null.</p>
 */
public class InvocationCommandException extends Exception {

    public InvocationCommandException(Throwable cause) {
        super(checkNotNull(cause));
    }

    public InvocationCommandException(String message, Throwable cause) {
        super(message, checkNotNull(cause));
    }

}
