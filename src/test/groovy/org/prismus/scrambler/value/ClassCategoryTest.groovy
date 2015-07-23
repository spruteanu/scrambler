package org.prismus.scrambler.value

import org.prismus.scrambler.DataScrambler
import spock.lang.Specification

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class ClassCategoryTest extends Specification {

    void 'verify categories registration'() {
        given:
        DummyObject.metaClass.mixin DataScrambler//, ClassCategory , NumberCategory, DateCategory, MapCategory, CollectionCategory
        DummyObject[].metaClass.mixin DataScrambler//, ClassCategory, NumberCategory, DateCategory, MapCategory, CollectionCategory

        expect:
        DummyObject == new DummyObject().constant().get().getClass()
        new DummyObject[10].randomOf().getValues().getClass == [] as DummyObject[]
    }

    class DummyObject {

    }

}
