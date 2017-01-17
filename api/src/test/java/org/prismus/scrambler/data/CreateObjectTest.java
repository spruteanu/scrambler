package org.prismus.scrambler.data;

import org.junit.Test;
import org.prismus.scrambler.InstanceScrambler;
import org.prismus.scrambler.data.beans.Car;

public class CreateObjectTest {

    @Test
    public void createObjectOrphanGetter() {
        InstanceData<Car> items = InstanceScrambler.instanceOf(Car.class);
        Car car = items.next();
        System.out.println(car.getWheelSize());
    }
}
