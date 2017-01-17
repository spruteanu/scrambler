package org.prismus.scrambler.data.beans;

/**
 * Fake getter
 */
public class Car {

    private Wheel wheel;

    public Wheel getWheel() {
        return wheel;
    }

    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }

    public int getWheelSize() {
        return wheel.getSize();
    }
}
