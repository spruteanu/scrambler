package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomFloat extends RandomRange<Float> {
    RandomFloat() {
        this(null, null)
    }

    RandomFloat(String name) {
        this(name, null)
    }

    RandomFloat(String name, Float value) {
        super(name, value)
        usingDefaults(0F, Float.MAX_VALUE)
    }

    RandomFloat(String name, Float minimum, Float maximum) {
        super(name, minimum, maximum)
        usingDefaults(0F, Float.MAX_VALUE)
    }

    @Override
    Float value() {
        final Float value = super.value()
        return new RandomDouble(getName(), value != null ? value.doubleValue() : null)
                .usingDefaults(defaultMinimum.doubleValue(), defaultMaximum.doubleValue())
                .between(
                        minimum != null ? minimum.doubleValue() : null,
                        maximum != null ? maximum.doubleValue() : null
                ).value().floatValue()
    }
}
