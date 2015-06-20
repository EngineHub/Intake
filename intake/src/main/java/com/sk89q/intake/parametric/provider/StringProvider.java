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

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.annotation.Validate;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

class StringProvider implements Provider<String> {

    static final StringProvider INSTANCE = new StringProvider();

    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public String get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException {
        String v = arguments.next();
        validate(v, modifiers);
        return v;
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return Collections.emptyList();
    }

    /**
     * Validate a string value using relevant modifiers.
     *
     * @param string the string
     * @param modifiers the list of modifiers to scan
     * @throws ArgumentParseException on a validation error
     */
    protected static void validate(String string, List<? extends Annotation> modifiers) throws ArgumentParseException {
        if (string == null) {
            return;
        }

        for (Annotation modifier : modifiers) {
            if (modifier instanceof Validate) {
                Validate validate = (Validate) modifier;

                if (!validate.regex().isEmpty()) {
                    if (!string.matches(validate.regex())) {
                        throw new ArgumentParseException(
                                String.format(
                                        "The given text doesn't match the right format (technically speaking, the 'format' is %s)",
                                        validate.regex()));
                    }
                }
            }
        }
    }

}
