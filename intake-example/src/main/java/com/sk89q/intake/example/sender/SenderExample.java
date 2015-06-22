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

package com.sk89q.intake.example.sender;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sk89q.intake.Command;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.dispatcher.SimpleDispatcher;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.util.auth.AuthorizationException;

import java.util.Map;
import java.util.logging.Logger;

public final class SenderExample {

    private static final Logger log  = Logger.getLogger(SenderExample.class.getCanonicalName());

    private SenderExample() {
    }

    public static void main(String[] args) throws AuthorizationException, InvocationCommandException, CommandException {
        Map<String, User> users = new ImmutableMap.Builder<String, User>()
                .put("aaron", new User("Aaron"))
                .put("michelle", new User("Michelle"))
                .build();

        Namespace namespace = new Namespace();
        namespace.put("sender", users.get("aaron")); // Our sender

        Injector injector = Intake.createInjector();
        injector.install(new SenderModule(users));

        ParametricBuilder builder = new ParametricBuilder(injector);

        Dispatcher dispatcher = new SimpleDispatcher();
        builder.registerMethodsAsCommands(dispatcher, new SenderExample());

        dispatcher.call("greet", namespace, ImmutableList.<String>of());
        dispatcher.call("privmsg aaron", namespace, ImmutableList.<String>of());
        dispatcher.call("privmsg michelle", namespace, ImmutableList.<String>of());
        dispatcher.call("poke aaron", namespace, ImmutableList.<String>of());
        dispatcher.call("poke michelle", namespace, ImmutableList.<String>of());
    }

    @Command(aliases = "greet", desc = "Greet the sender")
    public void greet(@Sender User user) {
        user.message("Hi!");
    }

    @Command(aliases = "privmsg", desc = "Send a message to someone")
    public void privMsg(@Sender User user, User target) {
        target.message("Hi from " + user.getName());
    }

    @Command(aliases = "poke", desc = "Poke someone anonymously")
    public void poke(User target) {
        target.message("You've been poked!");
    }

}
