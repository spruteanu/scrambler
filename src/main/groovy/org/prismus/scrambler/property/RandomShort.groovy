package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomShort extends RandomRange<Short> {
    RandomShort() {
        this(null, null)
    }

    RandomShort(String name) {
        this(name, null)
    }

    RandomShort(String name, Short value) {
        super(name, value)
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE)
    }

    RandomShort(String name, Short minimum, Short maximum) {
        super(name, minimum, maximum)
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE)
    }

    @Override
    Short value() {
        final Short value = super.value()
        return new RandomInteger(getName(), value != null ? value.intValue() : null)
                .usingDefaults(defaultMinimum.intValue(), defaultMaximum.intValue())
                .between(
                        minimum != null ? minimum.intValue() : null,
                        maximum != null ? maximum.intValue() : null
                ).value().shortValue()
    }
}
