package org.prismus.scrambler.value;

import org.junit.Test;
import org.prismus.scrambler.InstanceScrambler;
import org.prismus.scrambler.value.beans.Car;

public class CreateObjectTest {

    @Test
    public void createObjectOrphanGetter() {
        InstanceValue<Car> items = InstanceScrambler.instanceOf(Car.class);
        Car car = items.next();
        System.out.println(car.getWheelSize());
    }
}
