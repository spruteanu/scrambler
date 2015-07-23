package org.prismus.scrambler.value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class GroovyDefinitionCallable extends AbstractDefinitionCallable {
    Closure closure
    ValueDefinition definition

    GroovyDefinitionCallable() {
    }

    GroovyDefinitionCallable(Closure closure) {
        this(closure, null)
    }

    GroovyDefinitionCallable(Closure closure, ValueDefinition definition) {
        this.closure = closure
        this.definition = definition
    }

    GroovyDefinitionCallable(ValueDefinition definition, Closure closure, ValueDefinition definition1) {
        super(definition)
        this.closure = closure
        definition = definition1
    }

    @Override
    ValueDefinition call() throws Exception {
        closure.rehydrate(definition, definition, definition).call()
        return definition
    }

}
