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

package com.sk89q.intake.parametric.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks other annotations as a "classifier."
 *
 * <p>Classifiers are special annotations that are used to differentiate
 * bindings for the same base type. A binding that has a classifier
 * defined will only provide values for parameters that have that
 * classifier, and the binding will also have precedence over
 * another binding that only handles the base type.</p>
 *
 * <p>If an annotation is not annotated with this annotation, then
 * it will be considered a "modifier" and will be available to
 * providers; however, it will not be considered in choosing
 * the most appropriate binding for a parameter.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Classifier {
}
