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

package com.sk89q.intake.example.parametric;

import com.google.common.collect.Sets;

import java.util.Set;

public class Subject {

    private final String name;
    private final Set<String> permissions = Sets.newHashSet();

    public Subject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean permit(String permission) {
        return permissions.add(permission);
    }

    public boolean may(String permission) {
        return permissions.contains(permission);
    }
}
