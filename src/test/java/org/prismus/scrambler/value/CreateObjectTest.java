package org.prismus.scrambler.value;

import org.junit.Test;
import org.prismus.scrambler.DataScrambler;
import org.prismus.scrambler.value.beans.Car;

public class CreateObjectTest {

    @Test
    public void createObjectOrphanGetter() {
        InstanceValue<Car> items = DataScrambler.instanceOf(Car.class);
        Car car = items.next();
        System.out.println(car.getWheelSize());
    }
}
