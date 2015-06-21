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
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.Arguments;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnumProviderTest {

    private final EnumProvider<Size> provider = new EnumProvider<Size>(Size.class);

    @Test
    public void testGet() throws Exception {
        assertThat(provider.get(Arguments.of("small"), ImmutableList.<Annotation>of()), is(Size.SMALL));
        assertThat(provider.get(Arguments.of("verylarge"), ImmutableList.<Annotation>of()), is(Size.VERY_LARGE));
        assertThat(provider.get(Arguments.of("very_large"), ImmutableList.<Annotation>of()), is(Size.VERY_LARGE));
    }

    @Test(expected = ArgumentParseException.class)
    public void testGetMissing() throws Exception {
        provider.get(Arguments.of("tiny"), ImmutableList.<Annotation>of());
    }

    @Test
    public void testGetSuggestions() throws Exception {
        assertThat(provider.getSuggestions(""), containsInAnyOrder("small", "medium", "large", "very_large"));
        assertThat(provider.getSuggestions("s"), containsInAnyOrder("small"));
        assertThat(provider.getSuggestions("la"), containsInAnyOrder("large"));
        assertThat(provider.getSuggestions("very"), containsInAnyOrder("very_large"));
        assertThat(provider.getSuggestions("verylarg"), containsInAnyOrder("very_large"));
        assertThat(provider.getSuggestions("very_"), containsInAnyOrder("very_large"));
        assertThat(provider.getSuggestions("tiny"), Matchers.<String>empty());
    }

    enum Size {
        SMALL,
        MEDIUM,
        LARGE,
        VERY_LARGE
    }

}