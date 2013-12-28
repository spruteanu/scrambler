package org.prismus.scrambler.property

import spock.lang.Specification

import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * @author Serge Pruteanu
 */
class UtilTest extends Specification {

    void 'replace wildcards to regex'(String property, String expected) {
        expect:
        expected == Util.replaceWildcards(property)

        where:
        property << ['test*', 'account*Party*sid', 'va?ue', '*Sid', 'myProperty', 'org.prismus.scrambler.*']
        expected << ['^test.*$', '^account.*Party.*sid$', '^va.ue$', '^.*Sid$', '^myProperty$', '^org\\.prismus\\.scrambler\\..*$']
    }

    void 'lookup generic type'() {
//        IncrementalInteger.genericSuperclass
//        ParameterizedType superClass = (ParameterizedType) Example2.class.getGenericSuperclass();
//        System.out.println(superClass.getActualTypeArguments()[0]);
//        System.out.println(superClass.getActualTypeArguments()[1]);
        for (TypeVariable typeParam : IncrementalInteger.typeParameters) {
            System.out.println(typeParam.getName());
            for (Type bound : typeParam.bounds) {
                System.out.println(bound);
            }
        }
        expect:
        1 == 1
    }

}
