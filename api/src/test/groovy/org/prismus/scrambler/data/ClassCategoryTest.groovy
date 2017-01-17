package org.prismus.scrambler.data

import org.prismus.scrambler.InstanceScrambler
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ClassCategoryTest extends Specification {

    void 'verify categories registration'() {
        given:
        GroovyDataDefinition.register()
        DummyObject.metaClass.mixin InstanceScrambler//, ClassCategory , NumberCategory, DateCategory, MapCategory, CollectionCategory
        DummyObject[].metaClass.mixin InstanceScrambler//, ClassCategory, NumberCategory, DateCategory, MapCategory, CollectionCategory

        expect:
        DummyObject == new DummyObject().constant().get().getClass()
        new DummyObject[10].randomOf().getArray().getClass == [] as DummyObject[]
    }

    class DummyObject {

    }

}
