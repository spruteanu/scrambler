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

package org.prismus.scrambler.data

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class GroovyDefinitionCallable extends AbstractDefinitionCallable {
    Closure closure
    DataDefinition definition

    GroovyDefinitionCallable() {
    }

    GroovyDefinitionCallable(Closure closure) {
        this(closure, null)
    }

    GroovyDefinitionCallable(Closure closure, DataDefinition definition) {
        this.closure = closure
        this.definition = definition
    }

    GroovyDefinitionCallable(DataDefinition definition, Closure closure, DataDefinition definition1) {
        super(definition)
        this.closure = closure
        definition = definition1
    }

    @Override
    DataDefinition call() throws Exception {
        closure.rehydrate(definition, definition, definition).call()
        return definition
    }

}
