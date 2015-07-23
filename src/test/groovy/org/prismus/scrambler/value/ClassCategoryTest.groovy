package org.prismus.scrambler.value

import spock.lang.Specification

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class ClassCategoryTest extends Specification {

    void 'verify categories registration'() {
        given:
        DummyObject.metaClass.mixin ObjectCategory, ClassCategory , NumberCategory, DateCategory //, MapValue, CollectionValue,
        DummyObject[].metaClass.mixin ObjectCategory, ClassCategory, NumberCategory, DateCategory //, MapValue, CollectionValue,

        expect:
        DummyObject == new DummyObject().constant().get().getClass()
        new DummyObject[10].randomOf().getValues().getClass == [] as DummyObject[]
    }

    class DummyObject {

    }

}
