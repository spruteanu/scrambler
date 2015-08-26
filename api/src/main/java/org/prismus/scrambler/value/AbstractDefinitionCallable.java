/*
 * Data Scrambler, Data Generation API
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.value;

import java.util.concurrent.Callable;

/**
 * Abstract class used to customize value definitions creation/registration. Before an instance will be created,
 * {@code definition} instance will be injected/set
 *
 * @author Serge Pruteanu
 */
public abstract class AbstractDefinitionCallable implements Callable<ValueDefinition> {
    private ValueDefinition definition;

    public AbstractDefinitionCallable() {
    }

    public AbstractDefinitionCallable(ValueDefinition definition) {
        this.definition = definition;
    }

    public ValueDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ValueDefinition definition) {
        this.definition = definition;
    }
}