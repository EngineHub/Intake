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

package com.sk89q.intake.example.parametric.model;

import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Map;

public class Universe {

    private final Map<String, Body> bodies = Maps.newHashMap();

    public void put(String name, Body body) {
        bodies.put(name, body);
    }

    @Nullable
    public Body get(String name) {
        Body body = bodies.get(name);
        if (body == null) {
            throw new IllegalArgumentException("Couldn't find body with name '" + name + "'");
        }

        return body;
    }

    public Body getIfPresent(String name) {
        return bodies.get(name);
    }

    public Map<String, Body> getPrefixedWith(String prefix) {
        ImmutableMap.Builder<String, Body> matching = new Builder<String, Body>();
        for (Map.Entry<String, Body> entry : bodies.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                matching.put(entry);
            }
        }
        return matching.build();
    }

    public void remove(String name) {
        bodies.remove(name);
    }

}
