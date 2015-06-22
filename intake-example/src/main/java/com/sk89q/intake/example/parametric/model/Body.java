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

package com.sk89q.intake.example.parametric.model;

import javax.annotation.Nullable;

public class Body {

    private CelestialType type;
    private double meanTemperature;
    @Nullable
    private String description;

    public CelestialType getType() {
        return type;
    }

    public Body setType(CelestialType type) {
        this.type = type;
        return this;
    }

    public double getMeanTemperature() {
        return meanTemperature;
    }

    public Body setMeanTemperature(double meanTemperature) {
        this.meanTemperature = meanTemperature;
        return this;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

}
