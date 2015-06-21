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

import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.example.shared.model.Body;
import com.sk89q.intake.example.shared.model.CelestialType;
import com.sk89q.intake.example.shared.model.Universe;
import com.sk89q.intake.example.shared.module.UniverseModule;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;
import com.sk89q.intake.util.auth.AuthorizationException;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class ParametricExample {

    private static final Logger log  = Logger.getLogger(ParametricExample.class.getCanonicalName());

    private ParametricExample() {
    }

    public static void main(String[] args) {
        Subject subject = new Subject("example_user");
        subject.permit("planet.settype");
        subject.permit("planet.settemp");
        subject.permit("planet.setdesc");
        subject.permit("planet.info");

        Universe universe = new Universe();
        universe.put("mercury", new Body().setType(CelestialType.PLANET));
        universe.put("venus", new Body().setType(CelestialType.PLANET));
        universe.put("earth", new Body().setType(CelestialType.PLANET));
        universe.put("mars", new Body().setType(CelestialType.PLANET));
        universe.put("jupiter", new Body().setType(CelestialType.PLANET));
        universe.put("saturn", new Body().setType(CelestialType.PLANET));
        universe.put("uranus", new Body().setType(CelestialType.PLANET));
        universe.put("neptune", new Body().setType(CelestialType.PLANET));
        universe.put("pluto", new Body().setType(CelestialType.PLANET));

        Namespace namespace = new Namespace();
        namespace.put(Subject.class, subject); // We'll use this to check authorization

        Injector injector = Intake.createInjector();
        injector.install(new PrimitivesModule());
        injector.install(new UniverseModule(universe));

        ParametricBuilder builder = new ParametricBuilder(injector);
        builder.setAuthorizer(new ExampleAuthorizer());

        Dispatcher dispatcher = new CommandGraph()
                .builder(builder)
                    .commands()
                    .group("planet")
                        .registerMethods(new UniverseCommands())
                        .parent()
                    .graph()
                .getDispatcher();

        executeCommand(namespace, dispatcher, "planet info pluto");
        executeCommand(namespace, dispatcher, "planet settype pluto dwarfplanet");
        executeCommand(namespace, dispatcher, "planet info pluto");
        executeCommand(namespace, dispatcher, "planet settype poseidon planet");
        executeCommand(namespace, dispatcher, "planet settype pluto unknown");
        executeCommand(namespace, dispatcher, "planet settemp mercury 167");
        executeCommand(namespace, dispatcher, "planet setdesc mercury Closest to the Sun"); // Use of @Text on the String parameter
        executeCommand(namespace, dispatcher, "planet info mercury");
        executeCommand(namespace, dispatcher, "planet info -f mercury"); // Use of a flag (-f)
        executeCommand(namespace, dispatcher, "planet settemp earth 59 -f"); // Use of a flag (-f)
        executeCommand(namespace, dispatcher, "planet info earth");
        executeCommand(namespace, dispatcher, "planet delete earth"); // Permission fail
    }

    private static void executeCommand(Namespace namespace, CommandCallable callable, String command) {
        System.out.println();
        System.out.println("--------------------");
        System.out.println("/" + command);

        try {
            callable.call(command, namespace, Collections.<String>emptyList());
        } catch (CommandException e) {
            System.out.println("Uh oh! Something is wrong: " + e.getMessage());
        } catch (AuthorizationException ignored) {
            System.out.println("I'm sorry, Dave. I'm afraid I can't do that.");
        } catch (InvocationCommandException e) {
            log.log(Level.WARNING, "Something happened while executing a command", e);
        }
    }

}
