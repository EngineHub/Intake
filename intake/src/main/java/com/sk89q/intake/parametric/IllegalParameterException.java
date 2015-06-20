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

/**
 * Thrown if there is an error with a parameter.
 */
public class IllegalParameterException extends ParametricException {

    public IllegalParameterException() {
        super();
    }

    public IllegalParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalParameterException(String message) {
        super(message);
    }

    public IllegalParameterException(Throwable cause) {
        super(cause);
    }

}
