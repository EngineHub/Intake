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


package com.sk89q.intake.parametric.provider;

import com.google.common.collect.ImmutableList;
import com.sk89q.intake.argument.Arguments;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.CommandContext;
import com.sk89q.intake.argument.MissingArgumentException;
import org.junit.Assume;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests the text provider shipped with Intake for correct behaviour.
 */
public class TextProviderTest {
    @Test
    public void testGetWithArgString() throws Exception {
        //given
        TextProvider provider = new TextProvider();
        String inputText = "some text";
        CommandArgs arguments = givenArgumentsFromText(inputText);
        //when
        String result = provider.get(arguments, ImmutableList.<Annotation>of());
        //then
        assertThat("result must be the input text exactly", result, is(inputText));
    }

    @Test
    public void testGetWithArgStringAndSpaceAtEnd() throws Exception {
        //given
        TextProvider provider = new TextProvider();
        String inputText = "some text ";
        CommandArgs arguments = givenArgumentsFromText(inputText);
        //when
        String result = provider.get(arguments, ImmutableList.<Annotation>of());
        //then
        assertThat("result must be the input text, with the trailing space", result, is("some text "));
    }

    @Test
    public void testGetWithEmptyArgString() throws Exception {
        //given
        TextProvider provider = new TextProvider();
        String inputText = "";
        CommandArgs arguments = givenArgumentsFromText(inputText);
        //when
        String result = provider.get(arguments, ImmutableList.<Annotation>of());
        //then
        assertThat("result must be an empty string", result, is(inputText));
    }

    @Test
    public void testGetWithSpaceArgString() throws Exception {
        //given
        TextProvider provider = new TextProvider();
        String inputText = " ";
        CommandArgs arguments = givenArgumentsFromText(inputText);
        //when
        String result = provider.get(arguments, ImmutableList.<Annotation>of());
        //then
        assertThat("result must be a space", result, is(inputText));
    }

    @Test(expected = MissingArgumentException.class)
    public void testGetWithOtherConsumedArgs() throws Exception {
        //given
        TextProvider provider = new TextProvider();
        String inputText = "before text";
        CommandArgs arguments = givenArgumentsFromText(inputText);
        Assume.assumeThat("first consumed arg is correct", arguments.next(), is("before"));
        Assume.assumeThat("second consumed arg is correct", arguments.next(), is("text"));
        //when
        provider.get(arguments, ImmutableList.<Annotation>of());
        //then a MissingArgumentException is thrown
    }

    @Test
    public void testGetWithOtherConsumedArgsAndSpace() throws Exception {
        //given
        TextProvider provider = new TextProvider();
        String inputText = "before text ";
        CommandArgs arguments = givenArgumentsFromText(inputText);
        Assume.assumeThat("first consumed arg is correct", arguments.next(), is("before"));
        Assume.assumeThat("second consumed arg is correct", arguments.next(), is("text"));
        //when
        String result = provider.get(arguments, ImmutableList.<Annotation>of());
        //then
        assertThat("result must be an empty string", result, is(""));
    }

    private CommandArgs givenArgumentsFromText(String inputText) {
        String[] split = CommandContext.split(inputText);
        return Arguments.of(split);
    }

}
