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
import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.context.CommandContext;
import com.sk89q.intake.context.CommandLocals;
import com.sk89q.intake.parametric.handler.ExceptionConverter;
import com.sk89q.intake.parametric.handler.InvokeHandler;
import com.sk89q.intake.parametric.handler.InvokeListener;
import com.sk89q.intake.util.auth.AuthorizationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An abstract implementation of {@link CommandCallable} that does everything
 * but the actual parameter binding and method calling.
 */
public abstract class AbstractParametricCommand implements CommandCallable {

    private final ParametricBuilder builder;
    private ImmutableList<? extends ParameterData<?>> parameters = ImmutableList.of();
    private ImmutableList<String> permissions = ImmutableList.of();
    private ImmutableSet<Character> valueFlags = ImmutableSet.of();
    private boolean ignoreUnusedFlags = false;
    private ImmutableSet<Character> unusedFlags = ImmutableSet.of();
    private ImmutableSet<Annotation> annotations = ImmutableSet.of();

    /**
     * Create a new instance.
     *
     * @param builder the builder
     */
    protected AbstractParametricCommand(ParametricBuilder builder) {
        checkNotNull(builder, "builder");
        this.builder = builder;
    }

    /**
     * Get the instance of the parametric builder.
     *
     * @return the parametric builder
     */
    protected ParametricBuilder getBuilder() {
        return builder;
    }

    /**
     * Get the parameters that have been set.
     *
     * @return the parameters
     */
    protected List<? extends ParameterData<?>> getParameters() {
        return parameters;
    }

    /**
     * Get the parameters.
     *
     * @return the parameters
     */
    protected List<String> getPermissions() {
        return permissions;
    }

    /**
     * Set the parameters that have been set.
     *
     * @param parameters the parameters
     */
    protected void setParameters(List<? extends ParameterData<?>> parameters) {
        this.parameters = ImmutableList.copyOf(parameters);
    }

    /**
     * Set the list of permissions, one of which must be met.
     *
     * @param permissions the permissions
     */
    protected void setPermissions(List<String> permissions) {
        this.permissions = ImmutableList.copyOf(permissions);
    }

    /**
     * Get the value flags.
     *
     * @return the value flags
     */
    protected Set<Character> getValueFlags() {
        return valueFlags;
    }

    protected void setValueFlags(Set<Character> valueFlags) {
        this.valueFlags = ImmutableSet.copyOf(valueFlags);
    }

    /**
     * Get whether unused flags should be ignored (and therefore not cause
     * an exception to be thrown).
     *
     * @return true if unused flags are to be ignored
     */
    protected boolean getIgnoreUnusedFlags() {
        return ignoreUnusedFlags;
    }

    /**
     * Set whether unused flags should be ignored (and therefore not cause
     * an exception to be thrown).
     *
     * @param ignoreUnusedFlags true if unused flags are to be ignored
     */
    protected void setIgnoreUnusedFlags(boolean ignoreUnusedFlags) {
        this.ignoreUnusedFlags = ignoreUnusedFlags;
    }

    /**
     * Get the list of flags that can be unused when parsing arguments.
     *
     * @return list of flags
     */
    protected Set<Character> getUnusedFlags() {
        return unusedFlags;
    }

    /**
     * Set the list of flags that can be unused when parsing arguments.
     *
     * @param unusedFlags list of flags
     */
    protected void setUnusedFlags(Set<Character> unusedFlags) {
        this.unusedFlags = ImmutableSet.copyOf(unusedFlags);
    }

    /**
     * Get the annotations that describe the command.
     *
     * @return the annotations
     */
    public ImmutableSet<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Set the annotations that describe the command.
     *
     * @param annotations the annotations
     */
    public void setAnnotations(Set<Annotation> annotations) {
        this.annotations = ImmutableSet.copyOf(annotations);
    }

    @Override
    public final boolean call(String stringArguments, CommandLocals locals, String[] parentCommands) throws CommandException, AuthorizationException {
        // Test permission
        if (!testPermission(locals)) {
            throw new AuthorizationException();
        }

        String calledCommand = parentCommands.length > 0 ? parentCommands[parentCommands.length - 1] : "_";
        String[] split = CommandContext.split(calledCommand + " " + stringArguments);
        CommandContext context = new CommandContext(split, valueFlags, false, locals);
        final Object[] args;

        // Provide help if -? is specified
        if (context.hasFlag('?')) {
            throw new InvalidUsageException(null, this, true);
        }

        try {
            boolean invoke = true;

            List<InvokeHandler> handlers = new ArrayList<InvokeHandler>();
            for (InvokeListener listener : getBuilder().getInvokeListeners()) {
                InvokeHandler handler = listener.createInvokeHandler();
                handlers.add(handler);
                if (!handler.preProcess(getAnnotations(), getParameters(), context)) {
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
                if (!handler.preInvoke(getAnnotations(), parameters, args, context)) {
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
                        AbstractParametricCommand.this.call(args);
                        return null;
                    }
                }).get();
            } catch (ExecutionException e) {
                throw e.getCause();
            }

            // postInvoke handlers
            for (InvokeHandler handler : handlers) {
                handler.postInvoke(getAnnotations(), parameters, args, context);
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

    /**
     * Called with parsed arguments.
     *
     * @param args the parsed arguments
     * @throws Exception on any error
     */
    protected abstract void call(Object[] args) throws Exception;

    @Override
    public boolean testPermission(CommandLocals locals) {
        for (String perm : permissions) {
            if (builder.getAuthorizer().testPermission(locals, perm)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> getSuggestions(String arguments, CommandLocals locals) throws CommandException {
        return builder.getDefaultCompleter().getSuggestions(arguments, locals);
    }

}
