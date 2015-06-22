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

package com.sk89q.intake.example.parametric;

import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.example.parametric.model.Body;
import com.sk89q.intake.example.parametric.model.CelestialType;
import com.sk89q.intake.example.parametric.model.Universe;
import com.sk89q.intake.parametric.annotation.Switch;
import com.sk89q.intake.parametric.annotation.Text;

public class UniverseCommands {

    private static double fahrenheitToCelsius(double temp) {
        return (temp -  32) * 5.0 / 9.0;
    }

    private static double celsiusToFahrenheit(double temp) {
        return temp * 9.0 / 5.0 + 32;
    }

    @Command(aliases = "settype", desc = "Set the type of an object")
    @Require("body.settype")
    public void setType(Body body, CelestialType type) {
        body.setType(type);
    }

    @Command(aliases = "settemp", desc = "Set the mean temperature of an object")
    @Require("body.settemp")
    public void setTemp(Body body, double meanTemp, @Switch('f') boolean inFahrenheit) {
        if (inFahrenheit) {
            meanTemp = fahrenheitToCelsius(meanTemp);
        }
        body.setMeanTemperature(meanTemp);
    }

    @Command(aliases = "setdesc", desc = "Set the description of an object")
    @Require("body.setdesc")
    public void setDesc(Body body, @Text String desc) {
        // @Text is a classifier that overrides the normal String provider
        // This @Text provider uses up the rest of the arguments
        body.setDescription(desc);
    }

    @Command(aliases = "info", desc = "Show information about an object")
    @Require("body.info")
    public void info(Body body, @Switch('f') boolean inFahrenheit) {
        System.out.println("type: " + body.getType());
        if (inFahrenheit) {
            System.out.println("mean temp: " + celsiusToFahrenheit(body.getMeanTemperature()) + " deg F");
        } else {
            System.out.println("mean temp: " + body.getMeanTemperature() + " deg C");
        }
        if (body.getDescription() != null) {
            System.out.println("desc: " + body.getDescription());
        }
    }

    @Command(aliases = "delete", desc = "Delete a celestial body")
    @Require("body.deathstar")
    public void delete(Universe universe, String name) {
        universe.remove(name);
    }

}
