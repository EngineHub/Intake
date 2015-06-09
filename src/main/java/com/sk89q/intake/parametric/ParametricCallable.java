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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.Chars;
import com.sk89q.intake.Command;
import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.Require;
import com.sk89q.intake.SettableDescription;
import com.sk89q.intake.context.CommandLocals;
import com.sk89q.intake.parametric.handler.InvokeListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The implementation of a {@link CommandCallable} for the
 * {@link ParametricBuilder}.
 */
class ParametricCallable extends AbstractParametricCommand {

    private final Object object;
    private final Method method;
    private final SettableDescription description;
    private final Require permissionAnnotation;

    /**
     * Create a new instance.
     *
     * @param builder the parametric builder
     * @param object the object to invoke on
     * @param method the method to invoke
     * @param definition the command definition annotation
     * @throws ParametricException thrown on an error
     */
    ParametricCallable(ParametricBuilder builder, Object object, Method method, Command definition) throws ParametricException {
        super(builder);

        checkNotNull(builder, "builder");
        checkNotNull(object, "object");
        checkNotNull(method, "method");
        checkNotNull(definition, "definition");

        this.object = object;
        this.method = method;

        Set<Annotation> commandAnnotations = ImmutableSet.copyOf(method.getAnnotations());

        // Parse parameters using a ParameterInspector
        Annotation[][] annotations = method.getParameterAnnotations();
        Type[] types = method.getGenericParameterTypes();
        ParameterBinder<Method> binder = new ParameterBinder<Method>(builder);
        for (int i = 0; i < types.length; i++) {
            binder.addParameter(types[i], annotations[i], method);
        }
        setParameters(binder.getParameters());
        setValueFlags(binder.getValueFlags());

        // @Command has fields that causes listed (or all flags) to not be
        // checked to see that they were consumed
        setIgnoreUnusedFlags(definition.anyFlags());
        setUnusedFlags(Sets.newHashSet(Chars.asList(definition.flags().toCharArray())));

        // Update the description
        description = new SettableDescription();
        Require permHint = method.getAnnotation(Require.class);
        if (permHint != null) {
            description.setPermissions(Arrays.asList(permHint.value()));
        }
        description.setParameters(binder.getUserProvidedParameters());
        description.setDescription(!definition.desc().isEmpty() ? definition.desc() : null);
        description.setHelp(!definition.help().isEmpty() ? definition.help() : null);
        description.overrideUsage(!definition.usage().isEmpty() ? definition.usage() : null);

        // ...using the listeners too
        for (InvokeListener listener : builder.getInvokeListeners()) {
            listener.updateDescription(commandAnnotations, binder.getParameters(), description);
        }

        // Get permissions annotation
        permissionAnnotation = method.getAnnotation(Require.class);
        setPermissions(Arrays.asList(permissionAnnotation.value()));

        setAnnotations(commandAnnotations);
    }

    @Override
    protected void call(Object[] args) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object, args);
    }

    @Override
    public SettableDescription getDescription() {
        return description;
    }

    @Override
    public boolean testPermission(CommandLocals locals) {
        return permissionAnnotation == null || super.testPermission(locals);
    }

}