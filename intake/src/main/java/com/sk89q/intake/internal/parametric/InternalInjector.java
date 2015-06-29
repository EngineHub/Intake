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

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.parametric.*;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.provider.DefaultModule;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class InternalInjector implements Injector {

    private final BindingList bindings = new BindingList();

    public InternalInjector() {
        install(new DefaultModule());
    }

    @Override
    public void install(Module module) {
        checkNotNull(module, "module");
        module.configure(new InternalBinder(bindings));
    }

    @Override
    @Nullable
    public <T> Binding<T> getBinding(Key<T> key) {
        return bindings.getBinding(key);
    }

    @Override
    @Nullable
    public <T> Binding<T> getBinding(Class<T> type) {
        return getBinding(Key.get(type));
    }

    @Override
    @Nullable
    public <T> Provider<T> getProvider(Key<T> key) {
        Binding<T> binding = getBinding(key);
        return binding != null ? binding.getProvider() : null;
    }

    @Override
    @Nullable
    public <T> Provider<T> getProvider(Class<T> type) {
        return getProvider(Key.get(type));
    }

    @Override
    public <T> T getInstance(Key<T> key, CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        Provider<T> provider = getProvider(key);
        if (provider != null) {
            return provider.get(arguments, modifiers);
        } else {
            throw new ProvisionException("No binding was found for " + key);
        }
    }

    @Override
    public <T> T getInstance(Class<T> type, CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        return getInstance(Key.get(type), arguments, modifiers);
    }

}
