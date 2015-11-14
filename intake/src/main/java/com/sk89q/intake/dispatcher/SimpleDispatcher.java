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

package com.sk89q.intake.dispatcher;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sk89q.intake.*;
import com.sk89q.intake.argument.CommandContext;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.Default;
import com.sk89q.intake.util.auth.AuthorizationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * A simple implementation of {@link Dispatcher}.
 */
public class SimpleDispatcher implements Dispatcher {

    private final Map<String, CommandMapping> commands = new HashMap<String, CommandMapping>();
    private final Description description;
    private CommandMapping defaultMapping;

    /**
     * Create a new instance.
     */
    public SimpleDispatcher() {
        List<Parameter> parameters = Lists.newArrayList();

        parameters.add(
                new ImmutableParameter.Builder()
                        .setName("subcommand")
                        .setOptionType(OptionType.positional())
                        .build());

        parameters.add(
                new ImmutableParameter.Builder()
                        .setName("...")
                        .setOptionType(OptionType.optionalPositional())
                        .build());

        description = new ImmutableDescription.Builder()
                .setParameters(parameters)
                .build();
    }

    @Override
    public void registerCommand(CommandCallable callable, String... alias) {
        this.registerCommand(callable, null, alias);
    }

    @Override
    public void registerDefaultCommand(CommandCallable callable, Default defaultDefinition, String... alias) {
        this.registerCommand(callable, defaultDefinition, alias);
    }

    protected void registerCommand(CommandCallable callable, @Nullable Default defaultDefinition, String... aliases) {
        CommandMapping mapping = new ImmutableCommandMapping(callable, aliases);

        if (defaultDefinition != null) {
            if (this.defaultMapping != null) {
                throw new IllegalStateException("SimpleDispatcher does not support replacing the default command mapping");
            }

            this.defaultMapping = mapping;

            // If this command is marked as "default only" we should skip registering the aliases.
            if (defaultDefinition.defaultOnly()) {
                return;
            }
        }

        // Check for replacements
        for (String alias : aliases) {
            String lower = alias.toLowerCase();
            if (commands.containsKey(lower)) {
                throw new IllegalArgumentException(
                    "Can't add the command '" + alias + "' because SimpleDispatcher does not support replacing commands");
            }
        }

        for (String alias : aliases) {
            String lower = alias.toLowerCase();
            commands.put(lower, mapping);
        }
    }

    @Override
    public Set<CommandMapping> getCommands() {
        return Collections.unmodifiableSet(new HashSet<CommandMapping>(commands.values()));
    }
    
    @Override
    public Set<String> getAliases() {
        return Collections.unmodifiableSet(commands.keySet());
    }
    
    @Override
    public Set<String> getPrimaryAliases() {
        Set<String> aliases = new HashSet<String>();
        for (CommandMapping mapping : getCommands()) {
            aliases.add(mapping.getPrimaryAlias());
        }
        return Collections.unmodifiableSet(aliases);
    }

    @Override
    public boolean contains(String alias) {
        return commands.containsKey(alias.toLowerCase());
    }

    @Override
    public CommandMapping get(String alias) {
        return commands.get(alias.toLowerCase());
    }

    @Override
    public boolean call(String arguments, Namespace namespace, List<String> parentCommands) throws CommandException, InvocationCommandException, AuthorizationException {
        // We have permission for this command if we have permissions for subcommands
        if (!testPermission(namespace)) {
            throw new AuthorizationException();
        }

        String[] split = CommandContext.split(arguments);
        Set<String> aliases = getPrimaryAliases();

        if (aliases.isEmpty()) {
            throw new InvalidUsageException("This command has no sub-commands.", this, parentCommands);
        } else if (split.length > 0) {
            String subCommand = split[0];
            String subArguments = Joiner.on(" ").join(Arrays.copyOfRange(split, 1, split.length));
            List<String> subParents = ImmutableList.<String>builder().addAll(parentCommands).add(subCommand).build();
            CommandMapping mapping = get(subCommand);

            if (mapping != null) {
                try {
                    mapping.getCallable().call(subArguments, namespace, subParents);
                } catch (AuthorizationException e) {
                    throw e;
                } catch (CommandException e) {
                    throw e;
                } catch (InvocationCommandException e) {
                    throw e;
                } catch (Throwable t) {
                    throw new InvocationCommandException(t);
                }

                return true;
            }

            if (this.defaultMapping != null) {
                try {
                    this.defaultMapping.getCallable().call(subArguments, namespace, subParents);
                } catch (AuthorizationException e) {
                    throw e;
                } catch (CommandException e) {
                    throw e;
                } catch (InvocationCommandException e) {
                    throw e;
                } catch (Throwable t) {
                    throw new InvocationCommandException(t);
                }

                return true;
            }
        }

        throw new InvalidUsageException("Please choose a sub-command.", this, parentCommands, true);
    }

    @Override
    public List<String> getSuggestions(String arguments, Namespace locals) throws CommandException {
        String[] split = CommandContext.split(arguments);

        if (split.length <= 1) {
            String prefix = split.length > 0 ? split[0] : "";

            List<String> suggestions = new ArrayList<String>();

            for (CommandMapping mapping : getCommands()) {
                if (mapping.getCallable().testPermission(locals)) {
                    for (String alias : mapping.getAllAliases()) {
                        if (prefix.isEmpty() || alias.startsWith(arguments)) {
                            suggestions.add(mapping.getPrimaryAlias());
                            break;
                        }
                    }
                }
            }

            return suggestions;
        } else {
            String subCommand = split[0];
            CommandMapping mapping = get(subCommand);
            String passedArguments = Joiner.on(" ").join(Arrays.copyOfRange(split, 1, split.length));

            if (mapping != null) {
                return mapping.getCallable().getSuggestions(passedArguments, locals);
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public boolean testPermission(Namespace locals) {
        for (CommandMapping mapping : getCommands()) {
            if (mapping.getCallable().testPermission(locals)) {
                return true;
            }
        }

        return false;
    }

}
