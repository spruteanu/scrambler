package org.prismus.scrambler.value

import org.prismus.scrambler.InstanceScrambler
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ClassCategoryTest extends Specification {

    void 'verify categories registration'() {
        given:
        GroovyValueDefinition.register()
        DummyObject.metaClass.mixin InstanceScrambler//, ClassCategory , NumberCategory, DateCategory, MapCategory, CollectionCategory
        DummyObject[].metaClass.mixin InstanceScrambler//, ClassCategory, NumberCategory, DateCategory, MapCategory, CollectionCategory

        expect:
        DummyObject == new DummyObject().constant().get().getClass()
        new DummyObject[10].randomOf().getValues().getClass == [] as DummyObject[]
    }

    class DummyObject {

    }

}
