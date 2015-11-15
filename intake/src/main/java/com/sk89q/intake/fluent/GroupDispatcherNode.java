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

package com.sk89q.intake.fluent;

import com.sk89q.intake.Command;
import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandMapping;
import com.sk89q.intake.dispatcher.SimpleDispatcher;
import com.sk89q.intake.group.At;
import com.sk89q.intake.group.Group;
import com.sk89q.intake.group.Root;
import com.sk89q.intake.parametric.ParametricBuilder;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A collection of grouped commands.
 */
public class GroupDispatcherNode extends AbstractDispatcherNode {

    private final ParametricBuilder builder;

    protected GroupDispatcherNode(CommandGraph graph, DispatcherNode parent, SimpleDispatcher dispatcher, ParametricBuilder builder) {
        super(graph, parent, dispatcher);
        this.builder = builder;
    }

    /**
     * {@inheritDoc}
     */
    public GroupDispatcherNode registerMethods(Object object) {
        return (GroupDispatcherNode) super.registerMethods(object);
    }

    /**
     * Register {@link Group}ed commands on an object.
     *
     * @param object the object containing the methods
     * @return this object
     */
    public GroupDispatcherNode registerGrouped(Object object)  {
        boolean classRegistered = this.registerClass(object);
        this.registerClassMethods(object, classRegistered);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public GroupDispatcherNode parent() {
        return (GroupDispatcherNode) super.parent();
    }

    /**
     * Get the root command graph.
     *
     * @return the root command graph
     */
    public CommandGraph graph() {
        return this.graph;
    }

    protected boolean registerClass(@Nonnull final Object object) {
        boolean result = false;

        final Group group = object.getClass().getAnnotation(Group.class);
        if (group != null) {
            for (final At at : group.value()) {
                checkArgument(!at.value().isEmpty(), "group cannot be empty");
                this.builder.registerMethodsAsCommands(this.getGroup(at.value()), object);
                result = true;
            }
        }

        if (object.getClass().getAnnotation(Root.class) != null) {
            this.builder.registerMethodsAsCommands(this.dispatcher, object);
        }

        return result;
    }

    protected void registerClassMethods(@Nonnull final Object object, final boolean classRegistered) {
        for (final Method method : object.getClass().getDeclaredMethods()) {
            final Command definition = method.getAnnotation(Command.class);
            if (definition != null) {
                final CommandCallable callable = this.builder.build(object, method);
                final Group group = method.getAnnotation(Group.class);
                if (group != null) {
                    for (At at : group.value()) {
                        checkArgument(!at.value().isEmpty(), "group cannot be empty");
                        this.getGroup(at.value()).registerCommand(callable, definition.aliases());
                    }
                }

                if (method.getAnnotation(Root.class) != null || !classRegistered) {
                    this.dispatcher.registerCommand(callable, definition.aliases());
                }
            }
        }
    }

    protected SimpleDispatcher getGroup(String group) {
        CommandMapping mapping = this.dispatcher.get(group);
        if (mapping == null) {
            SimpleDispatcher child = new SimpleDispatcher();
            this.dispatcher.registerCommand(child, group);
            return child;
        } else if (mapping.getCallable() instanceof SimpleDispatcher) {
            return (SimpleDispatcher) mapping.getCallable();
        } else {
            throw new IllegalStateException("Can't put group at '" + group + "' because there is an existing command there");
        }
    }
}
