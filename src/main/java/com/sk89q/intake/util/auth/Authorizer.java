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

package com.sk89q.intake.util.auth;

import com.sk89q.intake.argument.Namespace;

/**
 * Tests whether permission is granted.
 */
public interface Authorizer {

    /**
     * Tests whether permission is granted for the given context.
     *
     * @param namespace The namespace
     * @param permission The permission string
     * @return Whether the action is permitted
     */
    boolean testPermission(Namespace namespace, String permission);

}
