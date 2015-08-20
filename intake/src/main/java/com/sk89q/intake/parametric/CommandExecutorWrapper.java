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

import com.sk89q.intake.argument.CommandArgs;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps an ExecutorService into a CommandExecutor.
 */
public class CommandExecutorWrapper implements CommandExecutor {

    private final ExecutorService executorService;

    public CommandExecutorWrapper(ExecutorService executorService) {
        checkNotNull(executorService, "executorService");
        this.executorService = executorService;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task, CommandArgs args) {
        return executorService.submit(task);
    }

}
