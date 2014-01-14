package org.prismus.scrambler.value

import spock.lang.Specification

import java.lang.reflect.Array

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class ValueArrayTest extends Specification {

    void 'test array creation'() { // todo Serge: add real tests for value array
        int[] array = new int[3];
        Class arrayClass = array.class;
        Class arrayClassType = arrayClass.componentType;
        System.out.println(arrayClassType);
        Array.newInstance(arrayClassType, 3)
        expect:
        true
    }

}
