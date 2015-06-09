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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sk89q.intake.Parameter;
import com.sk89q.intake.parametric.annotation.Optional;
import com.sk89q.intake.parametric.annotation.Switch;
import com.sk89q.intake.parametric.binding.Binding;
import com.sk89q.intake.parametric.binding.BindingBehavior;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Binds parameters to a {@link Binding}.
 *
 * <p>For every parameter, {@link #addParameter(Type, Annotation[], Object)}
 * can be called with the parameter's type and annotations and ParameterBinder
 * will create respective {@link Parameter} objects for them with
 * appropriate {@link Binding}s.</p>
 *
 * @param <T> The type of the object associated with each parameter
 */
public class ParameterBinder<T> {

    private final ParametricBuilder builder;
    /**
     * A list of all parameters, including parameters that are filled it
     * automatically as well as parameters that the user must supply.
     */
    private final List<ParameterData<T>> parameters = Lists.newArrayList();
    /**
     * A list of parameters that users must supply a value for.
     */
    private final List<Parameter> userProvidedParameters = Lists.newArrayList();
    private final Set<Character> valueFlags = Sets.newHashSet();
    /**
     * Helps keep tracks of @Nullables that appear in the middle of a list
     * of parameters.
     */
    private int optionalParamCount = 0;

    /**
     * Create a new instance.
     *
     * @param builder the builder
     */
    public ParameterBinder(ParametricBuilder builder) {
        checkNotNull(builder, "builder");
        this.builder = builder;
    }

    /**
     * Add a parameter.
     *
     * @param type the type of the parameter
     * @param annotations the annotations on the parameter
     * @param target the object associated with the parameter (field, etc.)
     */
    public void addParameter(Type type, Annotation[] annotations, T target) {
        checkNotNull(type, "type");
        checkNotNull(annotations, "annotations");

        int index = parameters.size(); // Index of this parameter
        Binding binding = null;

        ParameterData<T> parameter = new ParameterData<T>(target);
        parameter.setName(getFriendlyName(type, parameter.getClassifier(), index));
        parameter.setType(type);
        parameter.setModifiers(annotations);

        // Apply annotations like @Switch and @Optional that affect how
        // this parameter is parsed from arguments
        for (Annotation annotation : annotations) {
            if (annotation instanceof Switch) {
                parameter.setFlag(((Switch) annotation).value(), type != boolean.class);

            } else if (annotation instanceof Optional) {
                parameter.setOptional(true);

                String[] value = ((Optional) annotation).value();
                if (value.length > 0) {
                    parameter.setDefaultValue(value);
                }

            } else if (parameter.getBinding() == null) {
                // See if that this annotation has a binding associated with it
                binding = builder.getBindings().get(annotation.annotationType());

                // Also set this annotation to be the classifier
                parameter.setClassifier(annotation);
            }
        }

        // Make note of all value flags
        if (parameter.isValueFlag()) {
            valueFlags.add(parameter.getFlag());
        }

        // So we don't have a binding form this parameter yet: let's find one
        // so we know how to parse this parameter from the argument stream
        if (binding == null) {
            binding = builder.getBindings().get(type);
        }

        // Verify that we indeed have a binding found
        if (binding != null) {
            parameter.setBinding(binding);
        } else {
            throw new ParametricException("Can't finding a binding for the parameter type '" + type + "' in\n" + target);
        }

        // Do some validation of this parameter
        validate(parameter, target, index + 1);

        // Keep track of optional parameters
        if (parameter.isOptional() && parameter.getFlag() == null) {
            optionalParamCount++;
        } else {
            if (optionalParamCount > 0 && parameter.isNonFlagConsumer()) {
                if (parameter.getConsumedCount() < 0) {
                    throw new ParametricException(
                            "Found an parameter using the binding " +
                                    parameter.getBinding().getClass().getCanonicalName() +
                                    "\nthat does not know how many arguments it consumes, but " +
                                    "it follows an optional parameter\nMethod: " +
                                    target);
                }
            }
        }

        parameters.add(parameter);

        if (parameter.isUserInput()) {
            userProvidedParameters.add(parameter);
        }
    }

    /**
     * Get an immutable list of parameters added.
     *
     * @return list of parameters
     */
    public ImmutableList<ParameterData<T>> getParameters() {
        return ImmutableList.copyOf(parameters);
    }

    /**
     * Get an immutable list of parameters that the user must
     * supply a value for.
     *
     * @return list of parameters
     */
    public ImmutableList<Parameter> getUserProvidedParameters() {
        return ImmutableList.copyOf(userProvidedParameters);
    }

    /**
     * Get an immutable list of value flags.
     *
     * @return list of value flags
     */
    public ImmutableSet<Character> getValueFlags() {
        return ImmutableSet.copyOf(valueFlags);
    }

    /**
     * Generate a name for a parameter.
     *
     * @param type the type
     * @param classifier the classifier
     * @param index the index
     * @return a generated name
     */
    private static String getFriendlyName(Type type, Annotation classifier, int index) {
        if (classifier != null) {
            return classifier.annotationType().getSimpleName().toLowerCase();
        } else {
            return type instanceof Class<?> ? ((Class<?>) type).getSimpleName().toLowerCase() : "unknown" + index;
        }
    }


    /**
     * Validate this parameter and its binding.
     */
    private static void validate(ParameterData<?> parameter, Object target, int parameterIndex) throws ParametricException {
        Binding binding = parameter.getBinding();

        // We can't have indeterminate consumers without @Switches otherwise
        // it may screw up parameter processing for later bindings
        BindingBehavior behavior = parameter.getBinding().getBehavior(parameter);
        boolean indeterminate = (behavior == BindingBehavior.INDETERMINATE);

        if (!parameter.isValueFlag() && indeterminate) {
            throw new ParametricException(
                    "@Switch missing for indeterminate consumer\n\n" +
                            "Notably:\nFor the type " + parameter.getType() + ", the binding " +
                            binding.getClass().getCanonicalName() +
                            "\nmay or may not consume parameters (isIndeterminateConsumer(" + parameter.getType() + ") = true)" +
                            "\nand therefore @Switch(flag) is required for parameter #" + parameterIndex + " of \n" +
                            target);
        }

        // getConsumedCount() better return -1 if the BindingBehavior is not CONSUMES
        if (behavior != BindingBehavior.CONSUMES && binding.getConsumedCount(parameter) != -1) {
            throw new ParametricException(
                    "getConsumedCount() does not return -1 for binding " +
                            binding.getClass().getCanonicalName() +
                            "\neven though its behavior type is " + behavior.name() +
                            "\nfor parameter #" + parameterIndex + " of \n" +
                            target);
        }

        // getConsumedCount() should not return 0 if the BindingBehavior is not PROVIDES
        if (behavior != BindingBehavior.PROVIDES && binding.getConsumedCount(parameter) == 0) {
            throw new ParametricException(
                    "getConsumedCount() must not return 0 for binding " +
                            binding.getClass().getCanonicalName() +
                            "\nwhen its behavior type is " + behavior.name() + " and not PROVIDES " +
                            "\nfor parameter #" + parameterIndex + " of \n" +
                            target);
        }
    }

}
