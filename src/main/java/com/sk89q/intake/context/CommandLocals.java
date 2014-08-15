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

package com.sk89q.intake.context;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This object holds contextual data for a command execution.
 */
public class CommandLocals {
    
    private final Map<Object, Object> locals = new HashMap<Object, Object>();

    /**
     * Test whether the given key exists.
     *
     * @param key the key
     * @return true if the key exists
     */
    public boolean containsKey(Object key) {
        return locals.containsKey(key);
    }

    /**
     * Test whether the given value exists.
     *
     * @param value the value
     * @return true if the value exists
     */
    public boolean containsValue(Object value) {
        return locals.containsValue(value);
    }

    /**
     * Returns the value specified by the given key.
     *
     * @param key the key
     * @return the value, which may be null, including when the key doesn't exist
     */
    @Nullable
    public Object get(Object key) {
        return locals.get(key);
    }

    /**
     * Get an object whose key will be the object's class.
     *
     * @param key the key
     * @param <T> the type of object
     * @return the value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> key) {
        return (T) locals.get(key);
    }

    /**
     * Set an contextual value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>,
     *         if the implementation supports <tt>null</tt> values.)
     * @throws UnsupportedOperationException if the <tt>put</tt> operation
     *         is not supported by this map
     * @throws ClassCastException if the class of the specified key or value
     *         prevents it from being stored in this map
     */
    @Nullable
    public Object put(Object key, Object value) {
        return locals.put(key, value);
    }

}
