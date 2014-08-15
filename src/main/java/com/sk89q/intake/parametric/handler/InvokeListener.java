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

import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.Require;
import com.sk89q.intake.SettableDescription;
import com.sk89q.intake.parametric.ParameterData;
import com.sk89q.intake.parametric.ParametricBuilder;

import java.lang.reflect.Method;

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
     * @return a new invocation handler
     */
    InvokeHandler createInvokeHandler();

    /**
     * During creation of a {@link CommandCallable} by a {@link ParametricBuilder},
     * this will be called in case the description needs to be updated.
     * 
     * @param object the object
     * @param method the method
     * @param parameters a list of parameters
     * @param description the description to be updated
     */
    void updateDescription(Object object, Method method, ParameterData[] parameters, SettableDescription description);

}
