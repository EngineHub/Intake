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

import com.google.common.collect.ImmutableList;
import com.sk89q.intake.Command;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.SettableDescription;
import com.sk89q.intake.context.CommandContext;
import com.sk89q.intake.parametric.MissingParameterException;
import com.sk89q.intake.parametric.ParameterData;
import com.sk89q.intake.parametric.ParameterException;
import com.sk89q.intake.parametric.UnconsumedParameterException;
import com.sk89q.intake.parametric.binding.BindingBehavior;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * Handles legacy properties on {@link Command} such as {@link Command#min()} and
 * {@link Command#max()}.
 */
public class LegacyCommandsHandler extends AbstractInvokeListener implements InvokeHandler {

    @Override
    public InvokeHandler createInvokeHandler() {
        return this;
    }

    @Override
    public boolean preProcess(Set<Annotation> annotations, List<? extends ParameterData<?>> parameters, CommandContext context) throws CommandException, ParameterException {
        return true;
    }

    @Override
    public boolean preInvoke(Set<Annotation> annotations, List<? extends ParameterData<?>> parameters, Object[] args, CommandContext context) throws ParameterException, UnconsumedParameterException {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Command) {
                Command command = (Command) annotation;

                if (context.argsLength() < command.min()) {
                    throw new MissingParameterException();
                }

                if (command.max() != -1 && context.argsLength() > command.max()) {
                    throw new UnconsumedParameterException(context.getRemainingString(command.max()));
                }
            }
        }

        return true;
    }

    @Override
    public void postInvoke(Set<Annotation> annotations, List<? extends ParameterData<?>> parameters, Object[] args, CommandContext context) {
    }

    @Override
    public void updateDescription(Set<Annotation> annotations, List<? extends ParameterData<?>> parameters, SettableDescription description) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Command) {
                Command command = (Command) annotation;

                // Handle the case for old commands where no usage is set and all of its
                // parameters are provider bindings, so its usage information would
                // be blank and would imply that there were no accepted parameters
                if (command.usage().isEmpty() && (command.min() > 0 || command.max() > 0)) {
                    boolean hasUserParameters = false;

                    for (ParameterData parameter : parameters) {
                        if (parameter.getBinding().getBehavior(parameter) != BindingBehavior.PROVIDES) {
                            hasUserParameters = true;
                            break;
                        }
                    }

                    if (!hasUserParameters) {
                        description.overrideUsage("(unknown usage information)");
                    }
                }
            }
        }
    }

}
