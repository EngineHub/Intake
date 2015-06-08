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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Parameter;
import com.sk89q.intake.context.CommandContext;
import com.sk89q.intake.parametric.argument.ArgumentStack;
import com.sk89q.intake.parametric.argument.ContextArgumentStack;
import com.sk89q.intake.parametric.argument.StringArgumentStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Takes a list of {@link Parameter}s and parses arguments out of a
 * {@link CommandContext}.
 */
public class ParameterConsumer {

    private final ImmutableList<? extends ParameterData<?>> parameters;

    /**
     * Create a new instance.
     *
     * @param parameters the list of parameters
     */
    public ParameterConsumer(List<? extends ParameterData<?>> parameters) {
        checkNotNull(parameters, "parameters");
        this.parameters = ImmutableList.copyOf(parameters);
    }

    /**
     * Parse arguments out of the given context.
     *
     * @param context the context
     * @return the parsed arguments
     * @throws CommandException thrown on an error raised by the command
     * @throws InvocationTargetException thrown if invocation fails
     * @throws ConsumeException thrown if the conumption of an argument fails
     * @throws UnconsumedParameterException thrown if there are too many arguments
     */
    public Object[] parseArguments(CommandContext context) throws CommandException, InvocationTargetException, ConsumeException, UnconsumedParameterException {
        return parseArguments(context, false, ImmutableSet.<Character>of());
    }

    /**
     * Parse arguments out of the given context.
     *
     * @param context the context
     * @param ignoreUnusedFlags true to not check if there are consumed flags
     * @param unusedFlags list of flags to ignore if they are not consumed
     * @return the parsed arguments
     * @throws CommandException thrown on an error raised by the command
     * @throws InvocationTargetException thrown if invocation fails
     * @throws ConsumeException thrown if the conumption of an argument fails
     * @throws UnconsumedParameterException thrown if there are too many arguments
     */
    public Object[] parseArguments(CommandContext context, boolean ignoreUnusedFlags, Set<Character> unusedFlags) throws CommandException, InvocationTargetException, ConsumeException, UnconsumedParameterException {
        ContextArgumentStack arguments = new ContextArgumentStack(context);
        Object[] args = new Object[parameters.size()];

        // Collect parameters
        for (int i = 0; i < parameters.size(); i++) {
            ParameterData<?> parameter = parameters.get(i);

            try {
                if (mayConsumeArguments(i, arguments)) {
                    // Parse the user input into a method argument
                    ArgumentStack usedArguments = getScopedContext(parameter, arguments);

                    try {
                        args[i] = parameter.getBinding().bind(parameter, usedArguments, false);
                    } catch (MissingParameterException e) {
                        // Not optional? Then we can't execute this command
                        if (!parameter.isOptional()) {
                            throw e;
                        }

                        args[i] = getDefaultValue(i, arguments);
                    }
                } else {
                    args[i] = getDefaultValue(i, arguments);
                }
            } catch (ParameterException e) {
                throw new ConsumeException(parameter, e);
            }
        }

        // Check for unused arguments
        checkUnconsumed(arguments, ignoreUnusedFlags, unusedFlags);

        return args;
    }

    /**
     * Get the right {@link ArgumentStack}.
     *
     * @param parameter the parameter
     * @param existing the existing scoped context
     * @return the context to use
     */
    private static ArgumentStack getScopedContext(Parameter parameter, ArgumentStack existing) {
        if (parameter.getFlag() != null) {
            CommandContext context = existing.getContext();

            if (parameter.isValueFlag()) {
                return new StringArgumentStack(context, context.getFlag(parameter.getFlag()), false);
            } else {
                String v = context.hasFlag(parameter.getFlag()) ? "true" : "false";
                return new StringArgumentStack(context, v, true);
            }
        }

        return existing;
    }

    /**
     * Get whether a parameter is allowed to consume arguments.
     *
     * @param i the index of the parameter
     * @param scoped the scoped context
     * @return true if arguments may be consumed
     */
    private boolean mayConsumeArguments(int i, ContextArgumentStack scoped) {
        CommandContext context = scoped.getContext();
        ParameterData<?> parameter = parameters.get(i);

        // Flag parameters: Always consume
        // Required non-flag parameters: Always consume
        // Optional non-flag parameters:
        //     - Before required parameters: Consume if there are 'left over' args
        //     - At the end: Always consumes

        if (parameter.isOptional()) {
            if (parameter.getFlag() != null) {
                return !parameter.isValueFlag() || context.hasFlag(parameter.getFlag());
            } else {
                int numberFree = context.argsLength() - scoped.position();
                for (int j = i; j < parameters.size(); j++) {
                    if (parameters.get(j).isNonFlagConsumer() && !parameters.get(j).isOptional()) {
                        // We already checked if the consumed count was > -1
                        // when we created this object
                        numberFree -= parameters.get(j).getConsumedCount();
                    }
                }

                // Skip this optional parameter
                if (numberFree < 1) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Get the default value for a parameter.
     *
     * @param i the index of the parameter
     * @param scoped the scoped context
     * @return a value
     * @throws ParameterException on an error
     * @throws CommandException on an error
     */
    private Object getDefaultValue(int i, ContextArgumentStack scoped) throws ParameterException, CommandException, InvocationTargetException {
        CommandContext context = scoped.getContext();
        ParameterData<?> parameter = parameters.get(i);

        String[] defaultValue = parameter.getDefaultValue();
        if (defaultValue != null) {
            try {
                return parameter.getBinding().bind(parameter, new StringArgumentStack(context, defaultValue, false), false);
            } catch (MissingParameterException ignored) {
                throw new ParametricException(
                        "The default value '" + Arrays.toString(defaultValue) + "' of the parameter using the binding " +
                                parameter.getBinding().getClass() + " is invalid");
            }
        }

        return null;
    }


    /**
     * Check to see if all arguments, including flag arguments, were consumed.
     *
     * @param ignoreUnusedFlags true to not check if there are consumed flags
     * @param unusedFlags list of flags to ignore if they are not consumed
     * @param scoped the argument scope
     * @throws UnconsumedParameterException thrown if parameters were not consumed
     */
    private void checkUnconsumed(ContextArgumentStack scoped, boolean ignoreUnusedFlags, Set<Character> unusedFlags) throws UnconsumedParameterException {
        CommandContext context = scoped.getContext();
        String unconsumed;
        String unconsumedFlags = getUnusedFlags(context, ignoreUnusedFlags, unusedFlags);

        if ((unconsumed = scoped.getUnconsumed()) != null) {
            throw new UnconsumedParameterException(unconsumed + " " + unconsumedFlags);
        }

        if (unconsumedFlags != null) {
            throw new UnconsumedParameterException(unconsumedFlags);
        }
    }

    /**
     * Get any unused flag arguments.
     *
     * @param ignoreUnusedFlags true to not check if there are consumed flags
     * @param unusedFlags list of flags to ignore if they are not consumed
     * @param context the command context
     */
    private String getUnusedFlags(CommandContext context, boolean ignoreUnusedFlags, Set<Character> unusedFlags) {
        if (!ignoreUnusedFlags) {
            Set<Character> unconsumedFlags = null;
            for (char flag : context.getFlags()) {
                boolean found = false;

                if (unusedFlags.contains(flag)) {
                    break;
                }

                for (ParameterData<?> parameter : parameters) {
                    Character paramFlag = parameter.getFlag();
                    if (paramFlag != null && flag == paramFlag) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    if (unconsumedFlags == null) {
                        unconsumedFlags = new HashSet<Character>();
                    }
                    unconsumedFlags.add(flag);
                }
            }

            if (unconsumedFlags != null) {
                StringBuilder builder = new StringBuilder();
                for (Character flag : unconsumedFlags) {
                    builder.append("-").append(flag).append(" ");
                }

                return builder.toString().trim();
            }
        }

        return null;
    }

}
