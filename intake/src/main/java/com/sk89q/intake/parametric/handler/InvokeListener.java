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

package com.sk89q.intake.parametric.handler;

import com.sk89q.intake.ImmutableDescription;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.ArgumentParser;
import com.sk89q.intake.parametric.ParametricBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Listens to events related to {@link ParametricBuilder}.
 */
public interface InvokeListener {
    
    /**
     * Create a new invocation handler.
     * 
     * <p>An example use of an {@link InvokeHandler} would be to verify permissions
     * added by the {@link Require} annotation.</p>
     * 
     * <p>For simple {@link InvokeHandler}, an object can implement both this
     * interface and {@link InvokeHandler}.</p>
     * 
     * @return A new invocation handler
     */
    InvokeHandler createInvokeHandler();

    /**
     * Called to update the description of a command.
     *
     * @param annotations Annotations on the command
     * @param parser The parser containing parameter information
     * @param descriptionBuilder The description builder
     */
    void updateDescription(Set<Annotation> annotations, ArgumentParser parser, ImmutableDescription.Builder descriptionBuilder);

}
