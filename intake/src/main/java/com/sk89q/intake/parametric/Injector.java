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

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * An injector knows a list of "bindings" that map types to a provider.
 *
 * <p>For example, a command might accept an integer as an argument,
 * and so an appropriate binding for that parameter would have a provider
 * that parsed the argument as an integer and returned it.</p>
 */
public interface Injector {

    /**
     * Install a module into the injector. Modules define bindings.
     *
     * @param module The module
     */
    void install(Module module);

    /**
     * Get the binding for the given key, if one exists.
     *
     * @param key The key
     * @param <T> The type provided for
     * @return The binding, or null if one does not exist
     */
    @Nullable
    <T> Binding<T> getBinding(Key<T> key);

    /**
     * Get the binding for the given class, if one exists.
     *
     * @param type The class
     * @param <T> The type provided for
     * @return The binding, or null if one does not exist
     */
    @Nullable
    <T> Binding<T> getBinding(Class<T> type);

    /**
     * Get the provider for the given key, if one exists.
     *
     * @param key The key
     * @param <T> The type provided for
     * @return The binding, or null if one does not exist
     */
    @Nullable
    <T> Provider<T> getProvider(Key<T> key);

    /**
     * Get the provider for the given class, if one exists.
     *
     * @param type The class
     * @param <T> The type provided for
     * @return The binding, or null if one does not exist
     */
    @Nullable
    <T> Provider<T> getProvider(Class<T> type);

    /**
     * Attempt to provide a value for the given key using the given
     * arguments.
     *
     * @param key The key
     * @param arguments The arguments
     * @param modifiers The modifier annotations on the parameter
     * @param <T> The type provided
     * @return An instance
     * @throws ArgumentException If there is a problem with the argument
     * @throws ProvisionException If there is a problem with the provider
     */
    <T> T getInstance(Key<T> key, CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException;

    /**
     * Attempt to provide a value for the given class using the given
     * arguments.
     *
     * @param type The class
     * @param arguments The arguments
     * @param modifiers The modifier annotations on the parameter
     * @param <T> The type provided
     * @return An instance
     * @throws ArgumentException If there is a problem with the argument
     * @throws ProvisionException If there is a problem with the provider
     */
    <T> T getInstance(Class<T> type, CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException;

}
