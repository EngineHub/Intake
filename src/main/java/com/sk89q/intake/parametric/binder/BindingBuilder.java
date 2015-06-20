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

package com.sk89q.intake.parametric.binder;

import com.sk89q.intake.parametric.Provider;

import java.lang.annotation.Annotation;

/**
 * Part of the fluent binding creation interface.
 *
 * @param <T> The type being provided for
 */
public interface BindingBuilder<T> {

    /**
     * Indicates a classifier that the binding will listen for.
     *
     * @param annotation The classifier annotation class
     * @return The same class
     */
    BindingBuilder<T> annotatedWith(Class<? extends Annotation> annotation);

    /**
     * Creates a binding that is provided by the given provider class.
     *
     * @param provider The provider
     */
    void toProvider(Provider<T> provider);

    /**
     * Creates a binding that is provided by the given static instance.
     *
     * @param instance The instance
     */
    void toInstance(T instance);

}
