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

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public abstract class AbstractCommandArgsTest {

    protected abstract CommandArgs createCommandArgs(List<String> args);

    @Test
    public void testHasNext() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.<String>of());
        assertThat(args.position(), is(0));
        assertThat(args.hasNext(), is(false));
        assertThat(args.position(), is(0));

        args = createCommandArgs(ImmutableList.of("alpha"));
        assertThat(args.position(), is(0));
        assertThat(args.hasNext(), is(true));
        assertThat(args.position(), is(0));
    }

    @Test(expected = MissingArgumentException.class)
    public void testNextNoArgs() throws Exception {
        CommandArgs args = createCommandArgs(ImmutableList.<String>of());
        assertThat(args.position(), is(0));
        args.next();
    }

    @Test
    public void testNext() throws Exception {
        CommandArgs args = createCommandArgs(ImmutableList.of("alpha", "bravo"));
        assertThat(args.position(), is(0));
        assertThat(args.next(), equalTo("alpha"));
        assertThat(args.position(), is(1));
        assertThat(args.next(), equalTo("bravo"));
        assertThat(args.position(), is(2));
    }

    @Test(expected = MissingArgumentException.class)
    public void testPeekNoArgs() throws Exception {
        CommandArgs args = createCommandArgs(ImmutableList.<String>of());
        assertThat(args.position(), is(0));
        args.peek();
    }

    @Test
    public void testPeek() throws Exception {
        CommandArgs args = createCommandArgs(ImmutableList.of("alpha"));
        assertThat(args.position(), is(0));
        assertThat(args.peek(), equalTo("alpha"));
        assertThat(args.position(), is(0));
    }

    @Test
    public void testSize() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.<String>of());
        assertThat(args.size(), is(0));

        args = createCommandArgs(ImmutableList.of("alpha"));
        assertThat(args.size(), is(1));

        args = createCommandArgs(ImmutableList.of("alpha", "bravo"));
        assertThat(args.size(), is(2));
    }

    @Test
    public void testMarkConsumed() throws Exception {
        CommandArgs args = createCommandArgs(ImmutableList.of("alpha", "bravo"));
        assertThat(args.position(), is(0));
        args.markConsumed();
        assertThat(args.position(), is(2));
        args.markConsumed();
        assertThat(args.position(), is(2));
    }

    @Test(expected = ArgumentParseException.class)
    public void testNextBadInt() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("alpha"));
        assertThat(args.position(), is(0));
        args.nextInt();
    }

    @Test
    public void testNextInt() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("4000"));
        assertThat(args.position(), is(0));
        assertThat(args.nextInt(), is(4000));
        assertThat(args.position(), is(1));
    }

    @Test(expected = ArgumentParseException.class)
    public void testNextBadShort() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("alpha"));
        assertThat(args.position(), is(0));
        args.nextShort();
    }

    @Test
    public void testNextShort() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("4000"));
        assertThat(args.position(), is(0));
        assertThat(args.nextShort(), is((short) 4000));
        assertThat(args.position(), is(1));
    }

    @Test(expected = ArgumentParseException.class)
    public void testNextBadByte() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("alpha"));
        assertThat(args.position(), is(0));
        args.nextByte();
    }

    @Test
    public void testNextByte() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("40"));
        assertThat(args.position(), is(0));
        assertThat(args.nextByte(), is((byte) 40));
        assertThat(args.position(), is(1));
    }

    @Test(expected = ArgumentParseException.class)
    public void testNextBadDouble() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("alpha"));
        assertThat(args.position(), is(0));
        args.nextDouble();
    }

    @Test
    public void testNextDouble() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("40.23"));
        assertThat(args.position(), is(0));
        assertThat(args.nextDouble(), closeTo(40.23, 0.0001));
        assertThat(args.position(), is(1));
    }

    @Test(expected = ArgumentParseException.class)
    public void testNextBadFloat() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("alpha"));
        assertThat(args.position(), is(0));
        args.nextFloat();
    }

    @Test
    public void testNextFloat() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("40.23"));
        assertThat(args.position(), is(0));
        assertThat((double) args.nextFloat(), closeTo(40.23, 0.0001));
        assertThat(args.position(), is(1));
    }

    @Test(expected = ArgumentParseException.class)
    public void testNextBadBoolean() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("alpha"));
        assertThat(args.position(), is(0));
        args.nextBoolean();
    }

    @Test
    public void testNextBoolean() throws Exception {
        CommandArgs args;

        args = createCommandArgs(ImmutableList.of("true"));
        assertThat(args.position(), is(0));
        assertThat(args.nextBoolean(), is(true));
        assertThat(args.position(), is(1));

        args = createCommandArgs(ImmutableList.of("yes"));
        assertThat(args.position(), is(0));
        assertThat(args.nextBoolean(), is(true));
        assertThat(args.position(), is(1));

        args = createCommandArgs(ImmutableList.of("false"));
        assertThat(args.position(), is(0));
        assertThat(args.nextBoolean(), is(false));
        assertThat(args.position(), is(1));

        args = createCommandArgs(ImmutableList.of("no"));
        assertThat(args.position(), is(0));
        assertThat(args.nextBoolean(), is(false));
        assertThat(args.position(), is(1));
    }
}