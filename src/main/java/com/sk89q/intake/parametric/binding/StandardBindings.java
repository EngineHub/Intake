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

package com.sk89q.intake.parametric.binding;

import com.sk89q.intake.context.CommandContext;
import com.sk89q.intake.parametric.argument.ArgumentStack;

/**
 * Standard bindings that should be available to most configurations.
 */
public final class StandardBindings extends BindingHelper {

    /**
     * Gets a {@link CommandContext} from a {@link ArgumentStack}.
     * 
     * @param context the context
     * @return a selection
     */
    @BindingMatch(type = CommandContext.class,
                  behavior = BindingBehavior.PROVIDES)
    public CommandContext getCommandContext(ArgumentStack context) {
        context.markConsumed(); // Consume entire stack
        return context.getContext();
    }
    
}