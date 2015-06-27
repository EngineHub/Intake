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

import com.google.common.collect.ImmutableSet;
import com.sk89q.intake.CommandException;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CommandContextTest {

    public void testBuilderArguments() throws CommandException {
        CommandContext context = new CommandContext.Builder().setArguments(new String[] { "test" }).build();
        assertThat(context.getCommand(), equalTo("_"));
        assertThat(context.getString(0), equalTo("test"));
        assertThat(context.argsLength(), is(1));
    }

    public void testBuilderCommandAndArguments() throws CommandException {
        CommandContext context = new CommandContext.Builder().setCommandAndArguments(new String[] { "action", "test" }).build();
        assertThat(context.getCommand(), equalTo("action"));
        assertThat(context.getString(0), equalTo("test"));
        assertThat(context.argsLength(), is(1));
    }

    @Test
    public void testParsing() throws Exception {
        CommandContext context = new CommandContext("cmd alpha bravo");
        assertThat(context.getCommand(), is("cmd"));
        assertThat(context.getString(0), equalTo("alpha"));
        assertThat(context.getString(1), equalTo("bravo"));
        assertThat(context.hasFlag('a'), is(false));
        assertThat(context.hasFlag('b'), is(false));
        assertThat(context.hasFlag('c'), is(false));
        assertThat(context.hasFlag('d'), is(false));
        assertThat(context.argsLength(), is(2));
    }

    @Test
    public void testFlagParsing() throws Exception {
        CommandContext context = new CommandContext("cmd -ac alpha bravo -d");
        assertThat(context.getCommand(), is("cmd"));
        assertThat(context.getString(0), equalTo("alpha"));
        assertThat(context.getString(1), equalTo("bravo"));
        assertThat(context.hasFlag('a'), is(true));
        assertThat(context.hasFlag('b'), is(false));
        assertThat(context.hasFlag('c'), is(true));
        assertThat(context.hasFlag('d'), is(true));
        assertThat(context.argsLength(), is(2));
    }

    @Test
    public void testValueFlagParsing() throws Exception {
        CommandContext context = new CommandContext("cmd -ac alpha -v value bravo -d", ImmutableSet.of('v'));
        assertThat(context.getCommand(), is("cmd"));
        assertThat(context.getString(0), equalTo("alpha"));
        assertThat(context.getString(1), equalTo("bravo"));
        assertThat(context.hasFlag('a'), is(true));
        assertThat(context.hasFlag('b'), is(false));
        assertThat(context.hasFlag('c'), is(true));
        assertThat(context.hasFlag('d'), is(true));
        assertThat(context.hasFlag('v'), is(true));
        assertThat(context.getFlag('v'), equalTo("value"));
        assertThat(context.argsLength(), is(2));
    }

}
