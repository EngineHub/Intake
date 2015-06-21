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

import com.google.common.collect.Lists;
import com.sk89q.intake.CommandException;

import java.util.List;

public class ContextArgsTest extends AbstractCommandArgsTest {

    @Override
    protected CommandArgs createCommandArgs(List<String> args) {
        try {
            List<String> newArgs = Lists.newArrayList();
            newArgs.add("_");
            newArgs.addAll(args);
            String[] newArgsArray = new String[newArgs.size()];
            newArgs.toArray(newArgsArray);
            return new ContextArgs(new CommandContext(newArgsArray));
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }
    }
}