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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.intake.Command;
import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Require;
import com.sk89q.intake.completion.CommandCompleter;
import com.sk89q.intake.completion.NullCompleter;
import com.sk89q.intake.context.CommandContext;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.parametric.annotation.Switch;
import com.sk89q.intake.parametric.binding.Binding;
import com.sk89q.intake.parametric.binding.PrimitiveBindings;
import com.sk89q.intake.parametric.binding.StandardBindings;
import com.sk89q.intake.parametric.handler.ExceptionConverter;
import com.sk89q.intake.parametric.handler.InvokeHandler;
import com.sk89q.intake.parametric.handler.InvokeListener;
import com.sk89q.intake.util.auth.Authorizer;
import com.sk89q.intake.util.auth.NullAuthorizer;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Creates {@link CommandCallable}s from methods in an object that have been
 * annotated with {@link Command}. The types of the method's parameters
 * are read to determine the arguments that will be passed when the method
 * is invoked. The types of arguments that are supported (String, int,
 * domain-specific objects, etc.) depends on the {@link Binding}s that
 * are registered on this class.
 * 
 * @see Command defines a command
 * @see Switch defines a flag
 */
public class ParametricBuilder {

    private final Map<Type, Binding> bindings = new HashMap<Type, Binding>();
    private final List<InvokeListener> invokeListeners = new ArrayList<InvokeListener>();
    private final List<ExceptionConverter> exceptionConverters = new ArrayList<ExceptionConverter>();
    private Authorizer authorizer = new NullAuthorizer();
    private CommandCompleter defaultCompleter = new NullCompleter();
    private ExecutorService commandExecutor = MoreExecutors.sameThreadExecutor();
    
    /**
     * Create a new builder.
     * 
     * <p>This method will install {@link PrimitiveBindings} and 
     * {@link StandardBindings} and default bindings.</p>
     */
    public ParametricBuilder() {
        addBinding(new PrimitiveBindings());
        addBinding(new StandardBindings());
    }

    /**
     * Add a binding for a given type or classifier (annotation).
     * 
     * <p>Whenever a method parameter is encountered, a binding must be found for it
     * so that it can be called later to consume the stack of arguments provided by
     * the user and return an object that is later passed to 
     * {@link Method#invoke(Object, Object...)}.</p>
     * 
     * <p>Normally, a {@link Type} is used to discern between different bindings, but
     * if this is not specific enough, an annotation can be defined and used. This
     * makes it a "classifier" and it will take precedence over the base type. For
     * example, even if there is a binding that handles {@link String} parameters,
     * a special {@code @MyArg} annotation can be assigned to a {@link String}
     * parameter, which will cause the {@link Builder} to consult the {@link Binding}
     * associated with {@code @MyArg} rather than with the binding for
     * the {@link String} type.</p>
     * 
     * @param binding the binding
     * @param type a list of types (if specified) to override the binding's types
     */
    public void addBinding(Binding binding, @Nullable Type... type) {
        checkNotNull(binding);

        if (type == null || type.length == 0) {
            type = binding.getTypes();
        }
        
        for (Type t : type) {
            bindings.put(t, binding);
        }
    }
    
    /**
     * Attach an invocation listener.
     * 
     * <p>Invocation handlers are called in order that their listeners are
     * registered with a {@link ParametricBuilder}. It is not guaranteed that
     * a listener may be called, in the case of a {@link CommandException} being
     * thrown at any time before the appropriate listener or handler is called.
     * It is possible for a 
     * {@link InvokeHandler#preInvoke(Object, Method, ParameterData[], Object[], CommandContext)} to
     * be called for a invocation handler, but not the associated
     * {@link InvokeHandler#postInvoke(Object, Method, ParameterData[], Object[], CommandContext)}.</p>
     * 
     * <p>An example of an invocation listener is one to handle
     * {@link Require}, by first checking to see if permission is available
     * in a {@link InvokeHandler#preInvoke(Object, Method, ParameterData[], Object[], CommandContext)}
     * call. If permission is not found, then an appropriate {@link CommandException}
     * can be thrown to cease invocation.</p>
     * 
     * @param listener the listener
     * @see InvokeHandler the handler
     */
    public void addInvokeListener(InvokeListener listener) {
        checkNotNull(listener);
        invokeListeners.add(listener);
    }
    
    /**
     * Attach an exception converter to this builder in order to wrap unknown
     * {@link Throwable}s into known {@link CommandException}s.
     * 
     * <p>Exception converters are called in order that they are registered.</p>
     * 
     * @param converter the converter
     * @see ExceptionConverter for an explanation
     */
    public void addExceptionConverter(ExceptionConverter converter) {
        checkNotNull(converter);
        exceptionConverters.add(converter);
    }

    /**
     * Get the executor service used to invoke the actual command.
     *
     * <p>Bindings will still be resolved in the thread in which the
     * callable was called.</p>
     *
     * @return the command executor
     */
    public ExecutorService getCommandExecutor() {
        return commandExecutor;
    }

    /**
     * Set the executor service used to invoke the actual command.
     *
     * <p>Bindings will still be resolved in the thread in which the
     * callable was called.</p>
     *
     * @return the command executor
     */
    public void setCommandExecutor(ExecutorService commandExecutor) {
        checkNotNull(commandExecutor, "commandExecutor");
        this.commandExecutor = commandExecutor;
    }

    /**
     * Build a list of commands from methods specially annotated with {@link Command}
     * (and other relevant annotations) and register them all with the given
     * {@link Dispatcher}.
     * 
     * @param dispatcher the dispatcher to register commands with
     * @param object the object contain the methods
     * @throws ParametricException thrown if the commands cannot be registered
     */
    public void registerMethodsAsCommands(Dispatcher dispatcher, Object object) throws ParametricException {
        checkNotNull(dispatcher);
        checkNotNull(object);

        for (Method method : object.getClass().getDeclaredMethods()) {
            Command definition = method.getAnnotation(Command.class);
            if (definition != null) {
                CommandCallable callable = build(object, method, definition);
                dispatcher.registerCommand(callable, definition.aliases());
            }
        }
    }

    /**
     * Build a {@link CommandCallable} for the given method.
     * 
     * @param object the object to be invoked on
     * @param method the method to invoke
     * @param definition the command definition annotation
     * @return the command executor
     * @throws ParametricException thrown on an error
     */
    private CommandCallable build(Object object, Method method, Command definition) throws ParametricException {
        checkNotNull(object);
        checkNotNull(method);
        return new ParametricCallable(this, object, method, definition);
    }
    
    /**
     * Get the map of bindings.
     * 
     * @return the map of bindings
     */
    Map<Type, Binding> getBindings() {
        return bindings;
    }

    /**
     * Get a list of invocation listeners.
     * 
     * @return a list of invocation listeners
     */
    List<InvokeListener> getInvokeListeners() {
        return invokeListeners;
    }

    /**
     * Get the list of exception converters.
     * 
     * @return a list of exception converters
     */
    List<ExceptionConverter> getExceptionConverters() {
        return exceptionConverters;
    }

    /**
     * Get the authorizer.
     *
     * @return the authorizer
     */
    public Authorizer getAuthorizer() {
        return authorizer;
    }

    /**
     * Set the authorizer.
     *
     * @param authorizer the authorizer
     */
    public void setAuthorizer(Authorizer authorizer) {
        checkNotNull(authorizer);
        this.authorizer = authorizer;
    }

    /**
     * Get the default command suggestions provider that will be used if
     * no suggestions are available.
     *
     * @return the default command completer
     */
    public CommandCompleter getDefaultCompleter() {
        return defaultCompleter;
    }

    /**
     * Set the default command suggestions provider that will be used if
     * no suggestions are available.
     *
     * @param defaultCompleter the default command completer
     */
    public void setDefaultCompleter(CommandCompleter defaultCompleter) {
        checkNotNull(defaultCompleter);
        this.defaultCompleter = defaultCompleter;
    }

}
