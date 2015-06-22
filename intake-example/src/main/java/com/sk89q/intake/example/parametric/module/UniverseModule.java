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

package com.sk89q.intake.example.parametric.module;

import com.sk89q.intake.example.parametric.model.Body;
import com.sk89q.intake.example.parametric.model.CelestialType;
import com.sk89q.intake.example.parametric.model.Universe;
import com.sk89q.intake.parametric.AbstractModule;
import com.sk89q.intake.parametric.provider.EnumProvider;

public class UniverseModule extends AbstractModule {

    private final Universe universe;

    public UniverseModule(Universe universe) {
        this.universe = universe;
    }

    @Override
    protected void configure() {
        bind(Universe.class).toInstance(universe);
        bind(Body.class).toProvider(new BodyProvider(universe));
        bind(CelestialType.class).toProvider(new EnumProvider<CelestialType>(CelestialType.class));
    }

}
