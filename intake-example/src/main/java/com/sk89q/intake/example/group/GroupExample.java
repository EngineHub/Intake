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

package com.sk89q.intake.example.group;

import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.example.group.command.TestCommandsA;
import com.sk89q.intake.example.group.command.TestCommandsB;
import com.sk89q.intake.example.group.command.TestCommandsC;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.util.auth.AuthorizationException;

import java.util.Collections;

public class GroupExample {

    public static void main(String[] appArgs) {
        Injector injector = Intake.createInjector();
        ParametricBuilder builder = new ParametricBuilder(injector);
        Dispatcher dispatcher = new CommandGraph()
            .builder(builder)
            .groupedCommands()
            .registerGrouped(new TestCommandsA())
            .registerGrouped(new TestCommandsB())
            .registerGrouped(new TestCommandsC())
            .getDispatcher();

        Namespace namespace = new Namespace();

        executeCommand(namespace, dispatcher, "cat meow");
        executeCommand(namespace, dispatcher, "kitty meow");
        executeCommand(namespace, dispatcher, "cat meow");
        executeCommand(namespace, dispatcher, "kitty purr");
        executeCommand(namespace, dispatcher, "action hiss");
        executeCommand(namespace, dispatcher, "action sleep");
        executeCommand(namespace, dispatcher, "hiss");
        executeCommand(namespace, dispatcher, "meow");
        executeCommand(namespace, dispatcher, "action scratch");
        executeCommand(namespace, dispatcher, "lick");
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
            System.out.println("Something happened while executing a command");
            e.printStackTrace();
        }
    }
}
