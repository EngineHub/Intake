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

package com.sk89q.intake.internal.parametric;

import com.sk89q.intake.parametric.Key;
import com.sk89q.intake.parametric.annotation.Classifier;
import com.sk89q.intake.parametric.binder.BindingBuilder;
import com.sk89q.intake.parametric.Provider;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;

import static com.google.common.base.Preconditions.checkNotNull;

class InternalBinderBuilder<T> implements BindingBuilder<T> {

    private final BindingList bindings;
    private Key<T> key;

    public InternalBinderBuilder(BindingList bindings, Key<T> key) {
        checkNotNull(bindings, "bindings");
        checkNotNull(key, "key");
        this.bindings = bindings;
        this.key = key;
    }

    @Override
    public BindingBuilder<T> annotatedWith(@Nullable Class<? extends Annotation> annotation) {
        if (annotation != null) {
            if (annotation.getAnnotation(Classifier.class) == null) {
                throw new IllegalArgumentException("The annotation type " + annotation.getName() + " must be marked with @" + Classifier.class.getName() + " to be used as a classifier");
            }

            if (annotation.getAnnotation(Retention.class) == null) {
                throw new IllegalArgumentException("The annotation type " + annotation.getName() + " must be marked with @" + Retention.class.getName() + " to appear at runtime");
            }
        }
        key = key.setClassifier(annotation);
        return this;
    }

    @Override
    public void toProvider(Provider<T> provider) {
        bindings.addBinding(key, provider);
    }

    @Override
    public void toInstance(T instance) {
        toProvider(new ConstantProvider<T>(instance));
    }

}
