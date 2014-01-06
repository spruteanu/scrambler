package org.prismus.scrambler.property

import spock.lang.Specification

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

//    void 'lookup generic type'() {
////        IncrementalInteger.genericSuperclass
//        ParameterizedType superClass = (ParameterizedType) IncrementalInteger.class.getGenericSuperclass();
//        System.out.println(superClass.getActualTypeArguments()[0]);
//        expect:
//        1 == 1
//    }

}
