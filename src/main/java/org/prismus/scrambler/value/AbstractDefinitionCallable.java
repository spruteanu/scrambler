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
