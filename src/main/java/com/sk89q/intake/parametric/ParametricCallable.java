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
import com.google.common.primitives.Chars;
import com.sk89q.intake.*;
import com.sk89q.intake.context.CommandContext;
import com.sk89q.intake.context.CommandLocals;
import com.sk89q.intake.parametric.handler.ExceptionConverter;
import com.sk89q.intake.parametric.handler.InvokeHandler;
import com.sk89q.intake.parametric.handler.InvokeListener;
import com.sk89q.intake.util.auth.AuthorizationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The implementation of a {@link CommandCallable} for the
 * {@link ParametricBuilder}.
 */
class ParametricCallable implements CommandCallable {

    private final ParametricBuilder builder;
    private final Object object;
    private final Method method;
    private final ImmutableList<? extends ParameterData<?>> parameters;
    private final Set<Character> valueFlags = new HashSet<Character>();
    private final boolean ignoreUnusedFlags;
    private final Set<Character> unusedFlags = new HashSet<Character>();
    private final SettableDescription description = new SettableDescription();
    private final Require permission;

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
        checkNotNull(builder, "builder");
        checkNotNull(object, "object");
        checkNotNull(method, "method");
        checkNotNull(definition, "definition");

        this.builder = builder;
        this.object = object;
        this.method = method;

        // Parse parameters using a ParameterInspector
        Annotation[][] annotations = method.getParameterAnnotations();
        Type[] types = method.getGenericParameterTypes();
        ParameterBinder<Method> inspector = new ParameterBinder<Method>(builder);
        for (int i = 0; i < types.length; i++) {
            inspector.addParameter(types[i], annotations[i], method);
        }
        parameters = inspector.getParameters();

        // @Command has fields that causes listed (or all flags) to not be
        // checked to see that they were consumed
        ignoreUnusedFlags = definition.anyFlags();
        unusedFlags.addAll(Chars.asList(definition.flags().toCharArray()));

        // Update the description
        Require permHint = method.getAnnotation(Require.class);
        if (permHint != null) {
            description.setPermissions(Arrays.asList(permHint.value()));
        }
        description.setParameters(inspector.getUserProvidedParameters());
        description.setDescription(!definition.desc().isEmpty() ? definition.desc() : null);
        description.setHelp(!definition.help().isEmpty() ? definition.help() : null);
        description.overrideUsage(!definition.usage().isEmpty() ? definition.usage() : null);

        // ...using the listeners too
        for (InvokeListener listener : builder.getInvokeListeners()) {
            listener.updateDescription(object, method, parameters, description);
        }

        // Get permissions annotation
        permission = method.getAnnotation(Require.class);
    }

    @Override
    public boolean call(String stringArguments, CommandLocals locals, String[] parentCommands) throws CommandException, AuthorizationException {
        // Test permission
        if (!testPermission(locals)) {
            throw new AuthorizationException();
        }

        String calledCommand = parentCommands.length > 0 ? parentCommands[parentCommands.length - 1] : "_";
        String[] split = CommandContext.split(calledCommand + " " + stringArguments);
        CommandContext context = new CommandContext(split, getValueFlags(), false, locals);
        final Object[] args;

        // Provide help if -? is specified
        if (context.hasFlag('?')) {
            throw new InvalidUsageException(null, this, true);
        }

        try {
            boolean invoke = true;

            // preProcess handlers
            List<InvokeHandler> handlers = new ArrayList<InvokeHandler>();
            for (InvokeListener listener : builder.getInvokeListeners()) {
                InvokeHandler handler = listener.createInvokeHandler();
                handlers.add(handler);
                if (!handler.preProcess(object, method, parameters, context, locals)) {
                    invoke = false;
                }
            }

            if (!invoke) {
                return true; // Abort early
            }

            ParameterConsumer consumer = new ParameterConsumer(parameters);
            args = consumer.parseArguments(context, ignoreUnusedFlags, unusedFlags);

            // preInvoke handlers
            for (InvokeHandler handler : handlers) {
                if (!handler.preInvoke(object, method, parameters, args, context, locals)) {
                    invoke = false;
                }
            }

            if (!invoke) {
                return true; // Abort early
            }

            // Execute!
            try {
                builder.getCommandExecutor().submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return method.invoke(object, args);
                    }
                }).get();
            } catch (ExecutionException e) {
                throw e.getCause();
            }

            // postInvoke handlers
            for (InvokeHandler handler : handlers) {
                handler.postInvoke(handler, method, parameters, args, context, locals);
            }
        } catch (MissingParameterException ignored) {
            throw new InvalidUsageException("Too few parameters!", this);
        } catch (UnconsumedParameterException e) {
            throw new InvalidUsageException("Too many parameters! Unused parameters: " + e.getUnconsumed(), this);
        } catch (ConsumeException e) {
            throw new InvalidUsageException("For parameter '" + e.getParameter().getName() + "': " + e.getMessage(), this);
        } catch (InvocationTargetException e) {
            for (ExceptionConverter converter : builder.getExceptionConverters()) {
                converter.convert(e.getCause());
            }
            throw new InvocationCommandException(e);
        } catch (IllegalArgumentException e) {
            throw new InvocationCommandException(e);
        } catch (CommandException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationCommandException(e);
        }

        return true;
    }

    @Override
    public List<String> getSuggestions(String arguments, CommandLocals locals) throws CommandException {
        return builder.getDefaultCompleter().getSuggestions(arguments, locals);
    }

    /**
     * Get a list of value flags used by this command.
     *
     * @return a list of value flags
     */
    public Set<Character> getValueFlags() {
        return valueFlags;
    }

    @Override
    public SettableDescription getDescription() {
        return description;
    }

    @Override
    public boolean testPermission(CommandLocals locals) {
        if (permission != null) {
            for (String perm : permission.value()) {
                if (builder.getAuthorizer().testPermission(locals, perm)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    /**
     * Generate a name for a parameter.
     *
     * @param type the type
     * @param classifier the classifier
     * @param index the index
     * @return a generated name
     */
    private static String generateName(Type type, Annotation classifier, int index) {
        if (classifier != null) {
            return classifier.annotationType().getSimpleName().toLowerCase();
        } else {
            if (type instanceof Class<?>) {
                return ((Class<?>) type).getSimpleName().toLowerCase();
            } else {
                return "unknown" + index;
            }
        }
    }

}