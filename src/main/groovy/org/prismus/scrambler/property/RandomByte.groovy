package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomByte extends RandomRange<Byte> {
    RandomByte() {
        this(null, null)
    }

    RandomByte(String name) {
        this(name, null)
    }

    RandomByte(String name, Byte value) {
        super(name, value)
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE)
    }

    RandomByte(String name, Byte minimum, Byte maximum) {
        super(name, minimum, maximum)
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE)
    }

    @Override
    Byte value() {
        final Byte value = super.value()
        return new RandomInteger(getName(), value != null ? value.intValue() : null)
                .usingDefaults(defaultMinimum.intValue(), defaultMaximum.intValue())
                .between(
                        minimum != null ? minimum.intValue() : null,
                        maximum != null ? maximum.intValue() : null
                ).value().byteValue()
    }
}
