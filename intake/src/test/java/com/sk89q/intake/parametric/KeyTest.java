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

import com.google.common.collect.Sets;
import com.sk89q.intake.parametric.annotation.Classifier;
import com.sk89q.intake.parametric.annotation.Text;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.TreeSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 * Tests correct behaviour of the Key class, currently only focusing on correct collection
 * behaviour.
 */
public class KeyTest {
    @Test
    public void testMultipleClassifiersInTreeSet() {
        //given
        Key<String> keyWithoutClassifier = Key.get(String.class);
        Key<String> keyWithClassifier = Key.get(String.class, Text.class);
        Key<String> keyWithAnotherClassifier = Key.get(String.class, MyClassifier.class);
        TreeSet<Key<?>> keySet = Sets.newTreeSet(); //this mimics BindingsList behaviour
        keySet.add(keyWithoutClassifier);
        assumeThat(keySet.add(keyWithAnotherClassifier), is(true));
        //when
        boolean added = keySet.add(keyWithClassifier);
        //then
        assertThat("two keys with different classifiers must be accepted in a single set", added, is(true));
        assertThat("other classifier must be contained in set",
                keySet.contains(Key.get(String.class, MyClassifier.class)), is(true));
    }

    @Classifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    private @interface MyClassifier {

    }
}
