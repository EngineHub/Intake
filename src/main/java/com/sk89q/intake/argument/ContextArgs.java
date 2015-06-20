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

package com.sk89q.intake.argument;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a view of a {@link CommandContext} as arguments.
 */
class ContextArgs extends AbstractCommandArgs {
    
    private final CommandContext context;
    private int position = 0;

    ContextArgs(CommandContext context) {
        checkNotNull(context, "context");
        this.context = context;
    }

    @Override
    public boolean hasNext() {
        return position < context.argsLength();
    }

    @Override
    public String next() throws MissingArgumentException {
        try {
            return context.getString(position++);
        } catch (IndexOutOfBoundsException ignored) {
            throw new MissingArgumentException();
        }
    }

    @Override
    public String peek() throws MissingArgumentException {
        try {
            return context.getString(position);
        } catch (IndexOutOfBoundsException ignored) {
            throw new MissingArgumentException();
        }
    }

    @Override
    public int position() {
        return position;
    }

    @Override
    public int size() {
        return context.argsLength();
    }

    @Override
    public void markConsumed() {
        position = context.argsLength();
    }

    @Override
    public Map<Character, String> getFlags() {
        return context.getFlagsMap();
    }

    @Override
    public Namespace getNamespace() {
        return context.getNamespace();
    }

}
