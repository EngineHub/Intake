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

package com.sk89q.intake.completion;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A "cache" of command completer instances
 */
public final class CompleterCache {

    /** A map of completer classes to completer instances. */
    private static final Map<Class<? extends CommandCompleter>, CommandCompleter> CACHE = Maps.newHashMap();

    /**
     * Clear the completer class cache.
     */
    public static void clear() {
        CACHE.clear();
    }

    /**
     * Get an instance of a {@link CommandCompleter}.
     *
     * <p>Depending on the value of <code>useSharedInstance</code>, the returned completer
     * can either be a new instance, or an existing cached instance.</p>
     *
     * @param clazz the completer class
     * @param useSharedInstance if we should use a shared instance of the class
     * @return the completer instance
     */
    public static CommandCompleter get(Class<? extends CommandCompleter> clazz, boolean useSharedInstance) {
        if (!useSharedInstance) {
            try {
                return clazz.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

            return null;
        }

        if (CACHE.containsKey(clazz)) {
            return CACHE.get(clazz);
        } else {
            try {
                CommandCompleter completer = clazz.newInstance();
                CACHE.put(clazz, completer);
                return completer;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
